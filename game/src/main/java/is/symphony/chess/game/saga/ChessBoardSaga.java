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
import is.symphony.chess.player.core.commands.UpdateRatingCommand;
import is.symphony.chess.player.core.queries.GetPlayerQuery;
import is.symphony.chess.player.models.Player;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Saga
public class ChessBoardSaga {

    public static final String GAME_ID_ASSOCIATION = "gameId";

    public static final String BOARD_ID_ASSOCIATION = "boardId";

    public static final String ENGINE_BOARD_ID_ASSOCIATION = "engineBoardId";

    private transient CommandGateway commandGateway;

    private transient ReactorQueryGateway queryGateway;

    private UUID gameId;

    private UUID boardId;

    private final Map<PlayerColor, UUID> engines = new HashMap<>();

    @Autowired
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setQueryGateway(final ReactorQueryGateway queryGateway) {
        this.queryGateway = queryGateway;
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
    public void handle(BoardAssociatedEvent boardAssociatedEvent) {
        boardId = boardAssociatedEvent.getBoardId();

        Flux.just(boardAssociatedEvent.getPlayerWhite(), boardAssociatedEvent.getPlayerBlack())
                .flatMap(uuid -> queryGateway.query(new GetPlayerQuery(uuid), Player.class))
                .filter(Player::isBot)
                .doOnNext(player -> {
                    if (player.getPlayerId().equals(boardAssociatedEvent.getPlayerWhite())) {
                        engines.put(PlayerColor.WHITE, player.getEngineId());
                    }
                    else {
                        engines.put(PlayerColor.BLACK, player.getEngineId());
                    }
                }).blockLast();

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
    public void handle(GameFinishedEvent gameFinishedEvent) {
        Flux.just(gameFinishedEvent.getWhitePlayer(), gameFinishedEvent.getBlackPlayer())
                .flatMapSequential(uuid -> queryGateway.query(new GetPlayerQuery(uuid), Player.class))
                .collectList()
                .flatMapMany(players -> {
                    int whiteRating = players.get(0).getRating();
                    int blackRating = players.get(1).getRating();

                    switch (gameFinishedEvent.getResult()) {
                        case "1/2-1/2":
                            players.get(0).setRating(EloRating.calculate(whiteRating, blackRating, 0.5));
                            players.get(1).setRating(EloRating.calculate(blackRating, whiteRating, 0.5));
                            break;
                        case "1-0":
                            players.get(0).setRating(EloRating.calculate(whiteRating, blackRating, 1));
                            players.get(1).setRating(EloRating.calculate(blackRating, whiteRating, 0));
                            break;
                        case "0-1":
                            players.get(0).setRating(EloRating.calculate(whiteRating, blackRating, 0));
                            players.get(1).setRating(EloRating.calculate(blackRating, whiteRating, 1));
                    }

                    return Flux.fromIterable(players);
                }).flatMap(player -> Mono.fromFuture(
                        commandGateway.<Player>send(new UpdateRatingCommand(player.getPlayerId(), player.getRating())))).blockLast();

        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameCanceledEvent gameCanceledEvent) {
        SagaLifecycle.end();
    }
}
