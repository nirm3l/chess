package is.symphony.chess.game.saga;

import is.symphony.chess.board.core.commands.CreateBoardCommand;
import is.symphony.chess.board.core.commands.DrawBoardGameCommand;
import is.symphony.chess.board.core.commands.FinishBoardGameCommand;
import is.symphony.chess.board.core.commands.PlayMoveCommand;
import is.symphony.chess.board.core.events.BoardCanceledEvent;
import is.symphony.chess.board.core.events.BoardGameFinishedEvent;
import is.symphony.chess.board.core.events.MovePlayedEvent;
import is.symphony.chess.board.core.models.PlayerColor;
import is.symphony.chess.engine.core.commands.FindBestMoveCommand;
import is.symphony.chess.engine.core.events.BestMoveEvent;
import is.symphony.chess.game.core.commands.AssociateGameWithBoardCommand;
import is.symphony.chess.game.core.commands.CancelGameCommand;
import is.symphony.chess.game.core.commands.FinishGameCommand;
import is.symphony.chess.game.core.events.*;
import is.symphony.chess.game.core.exceptions.ItsNotYourGameException;
import is.symphony.chess.game.utils.EloRating;
import is.symphony.chess.player.core.commands.RevertRatingCommand;
import is.symphony.chess.player.core.commands.UpdateRatingCommand;
import is.symphony.chess.player.core.events.PlayerRatingUpdateFailedEvent;
import is.symphony.chess.player.core.events.PlayerRatingUpdatedEvent;
import is.symphony.chess.player.core.queries.GetPlayerQuery;
import is.symphony.chess.player.models.Player;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Saga
public class ChessBoardSaga {

    private static final Logger LOG = LoggerFactory.getLogger(ChessBoardSaga.class);

    public static final String GAME_ID_ASSOCIATION = "gameId";

    public static final String BOARD_ID_ASSOCIATION = "boardId";

    public static final String ENGINE_BOARD_ID_ASSOCIATION = "engineBoardId";

    private transient CommandGateway commandGateway;

    private transient QueryGateway queryGateway;

    private transient EventScheduler eventScheduler;

    private UUID gameId;

    private UUID boardId;

    private String result;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    private final Map<UUID, Integer> playerRatingUpdatedDeltas = new HashMap<>();

    private final Map<PlayerColor, UUID> engines = new HashMap<>();

    private ScheduleToken retryUpdatePlayerRatingsToken;

    @Autowired
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Autowired
    public void setEventScheduler(final EventScheduler eventScheduler) {
        this.eventScheduler = eventScheduler;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameReadyEvent gameReadyEvent) {
        gameId = gameReadyEvent.getGameId();

        try {
            boardId = commandGateway.
                    sendAndWait(new CreateBoardCommand(gameReadyEvent.getMinutes(),
                    gameReadyEvent.getIncrementSeconds()));

            SagaLifecycle.associateWith(BOARD_ID_ASSOCIATION, boardId.toString());

            commandGateway.send(new AssociateGameWithBoardCommand(gameId, boardId));
        }
        catch (Exception e) {
            commandGateway.sendAndWait(new CancelGameCommand(gameId));
            SagaLifecycle.end();
        }
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(BoardGameFinishedEvent boardGameFinishedEvent) {
        commandGateway.send(new FinishGameCommand(gameId, boardGameFinishedEvent.getResult()));
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(BoardAssociatedEvent boardAssociatedEvent) throws ExecutionException, InterruptedException {
        boardId = boardAssociatedEvent.getBoardId();

        final CompletableFuture<Player> whitePlayerFuture = queryGateway.query(new GetPlayerQuery(boardAssociatedEvent.getPlayerWhite()), Player.class);
        final CompletableFuture<Player> blackPlayerFuture = queryGateway.query(new GetPlayerQuery(boardAssociatedEvent.getPlayerBlack()), Player.class);

        whitePlayerFuture.whenComplete((player, throwable) -> {
            if (player.isBot()) {
                engines.put(PlayerColor.WHITE, player.getEngineId());
            }
        });

        blackPlayerFuture.whenComplete((player, throwable) -> {
            if (player.isBot()) {
                engines.put(PlayerColor.BLACK, player.getEngineId());
            }
        });

        CompletableFuture.allOf(whitePlayerFuture, blackPlayerFuture).get();

        if (engines.size() > 0) {
            SagaLifecycle.associateWith(ENGINE_BOARD_ID_ASSOCIATION, boardId.toString());
        }

        if (engines.containsKey(PlayerColor.WHITE)) {
            playEngine(engines.get(PlayerColor.WHITE), null);
        }
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION, keyName = ENGINE_BOARD_ID_ASSOCIATION)
    public void handle(MovePlayedEvent movePlayedEvent) {
        UUID engineToPlay = null;

        if (movePlayedEvent.getBoardMove().getPlayerColor() == PlayerColor.WHITE) {
            engineToPlay = engines.get(PlayerColor.BLACK);
        }
        else if (movePlayedEvent.getBoardMove().getPlayerColor() == PlayerColor.BLACK) {
            engineToPlay = engines.get(PlayerColor.WHITE);
        }

        if (engineToPlay != null && movePlayedEvent.getBoardMove().getResult() == null) {
            playEngine(engineToPlay, movePlayedEvent.getBoardState());
        }
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(BestMoveEvent bestMoveEvent) {
        commandGateway.send(
                new PlayMoveCommand(boardId,
                        getPlayerColor(bestMoveEvent.getEngineId()),
                        bestMoveEvent.getMove()));
    }

    private PlayerColor getPlayerColor(UUID engineId) {
        for (Map.Entry<PlayerColor, UUID> entry : engines.entrySet()) {
            if (engineId.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new ItsNotYourGameException();
    }

    private void playEngine(UUID engineId, String fen) {
        commandGateway.send(new FindBestMoveCommand(engineId, gameId, fen));
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(BoardCanceledEvent boardCanceledEvent) {
        commandGateway.send(new CancelGameCommand(gameId));
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameResignedEvent gameResignedEvent) {
        commandGateway.send(new FinishBoardGameCommand(
                boardId, PlayerColor.valueOf(gameResignedEvent.getPlayerColor().toString())));
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameDrawEvent gameDrawEvent) {
        commandGateway.send(new DrawBoardGameCommand(
                boardId, PlayerColor.valueOf(gameDrawEvent.getPlayerColor().toString())));
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameFinishedEvent gameFinishedEvent) throws ExecutionException, InterruptedException {
        result = gameFinishedEvent.getResult();
        whitePlayerId = gameFinishedEvent.getWhitePlayer();
        blackPlayerId = gameFinishedEvent.getBlackPlayer();

        updateRatings();
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(RetryUpdatePlayerRatingsEvent retryUpdatePlayerRatingsEvent) throws ExecutionException, InterruptedException {
        updateRatings();
    }

    private void updateRatings() throws ExecutionException, InterruptedException {
        playerRatingUpdatedDeltas.clear();

        LOG.info("Update ratings for {}.", gameId);

        final Player whitePlayer = queryGateway.query(new GetPlayerQuery(whitePlayerId), Player.class).get();
        final Player blackPlayer = queryGateway.query(new GetPlayerQuery(blackPlayerId), Player.class).get();

        int whiteRating = whitePlayer.getRating();
        int blackRating = blackPlayer.getRating();

        switch (result) {
            case "1/2-1/2":
                whitePlayer.setRating(EloRating.calculate(whiteRating, blackRating, 0.5));
                blackPlayer.setRating(EloRating.calculate(blackRating, whiteRating, 0.5));
                break;
            case "1-0":
                whitePlayer.setRating(EloRating.calculate(whiteRating, blackRating, 1));
                blackPlayer.setRating(EloRating.calculate(blackRating, whiteRating, 0));
                break;
            case "0-1":
                whitePlayer.setRating(EloRating.calculate(whiteRating, blackRating, 0));
                blackPlayer.setRating(EloRating.calculate(blackRating, whiteRating, 1));
        }

        if (!whitePlayer.getRating().equals(whiteRating)) {
            commandGateway.send(new UpdateRatingCommand(
                    whitePlayerId, gameId, whitePlayer.getRating() - whiteRating, whitePlayer.getVersion()));
        }
        else {
            LOG.info("Game {} no update rating needed for player {}.", gameId, whitePlayerId);
            playerRatingUpdatedDeltas.put(whitePlayerId, Integer.MAX_VALUE);
        }

        if (!blackPlayer.getRating().equals(blackRating)) {
            commandGateway.send(new UpdateRatingCommand(
                    blackPlayerId, gameId, blackPlayer.getRating() - blackRating, blackPlayer.getVersion()));
        }
        else {
            LOG.info("Game {} no update rating needed for player {}.", gameId, blackPlayerId);
            playerRatingUpdatedDeltas.put(blackPlayerId, Integer.MAX_VALUE);
        }

        if (playerRatingUpdatedDeltas.size() == 2) {
            SagaLifecycle.end();
        }
    }

    private void retryUpdatePlayerRatings() {
        LOG.info("Game {} schedule update rating retry.", gameId);

        if (retryUpdatePlayerRatingsToken != null) {
            retryUpdatePlayerRatingsToken = eventScheduler.reschedule(
                    retryUpdatePlayerRatingsToken, Duration.ofSeconds(30), new RetryUpdatePlayerRatingsEvent(gameId));
        }
        else {
            retryUpdatePlayerRatingsToken = eventScheduler.schedule(
                    Duration.ofSeconds(30), new RetryUpdatePlayerRatingsEvent(gameId));
        }
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(PlayerRatingUpdatedEvent event) {
        if (!event.isReverted()) {
            playerRatingUpdatedDeltas.put(event.getPlayerId(), event.getRatingDelta());

            LOG.info("Game {} success update rating for player {}.", gameId, event.getPlayerId());

            if (playerRatingUpdatedDeltas.size() == 2) {
                if (playerRatingUpdatedDeltas.get(getOppositePlayerId(event.getPlayerId())) != 0)
                    SagaLifecycle.end();
                }
                else {
                    retryUpdatePlayerRatings();
                }
        }
        else {
            LOG.info("Game {} reverted update rating for player {}.", gameId, event.getPlayerId());

            retryUpdatePlayerRatings();
        }
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(PlayerRatingUpdateFailedEvent event) {
        LOG.info("Game {} failed update rating for player {}.", gameId, event.getPlayerId());

        playerRatingUpdatedDeltas.put(event.getPlayerId(), 0);

        if (playerRatingUpdatedDeltas.size() == 2) {
            final UUID oppositePlayerId = getOppositePlayerId(event.getPlayerId());

            if (playerRatingUpdatedDeltas.get(oppositePlayerId) == Integer.MAX_VALUE) {
                retryUpdatePlayerRatings();
            }
            else if (playerRatingUpdatedDeltas.get(oppositePlayerId) != 0) {
                LOG.info("Game {} reverting update rating for player {}.", gameId, oppositePlayerId);

                commandGateway.send(new RevertRatingCommand(oppositePlayerId, gameId, -1 * playerRatingUpdatedDeltas.get(oppositePlayerId)));
            }
            else {
                retryUpdatePlayerRatings();
            }
        }
    }

    private UUID getOppositePlayerId(final UUID playerId) {
        if (playerId.equals(whitePlayerId)) {
            return blackPlayerId;
        }
        else {
            return whitePlayerId;
        }
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameCanceledEvent gameCanceledEvent) {
        SagaLifecycle.end();
    }
}
