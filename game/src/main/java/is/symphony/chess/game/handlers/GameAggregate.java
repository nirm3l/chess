package is.symphony.chess.game.handlers;

import is.symphony.chess.game.core.commands.*;
import is.symphony.chess.game.core.events.*;
import is.symphony.chess.game.core.exceptions.*;
import is.symphony.chess.game.core.models.PlayerColor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Random;
import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "aggregateSnapshotTriggerDefinition")
public class GameAggregate {
    @AggregateIdentifier
    private UUID gameId;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    private UUID invitePlayerId;

    private UUID boardId;

    private Integer minutes;

    private Integer incrementSeconds;

    private String result;

    private boolean gameCanceled;

    protected GameAggregate() { }

    @CommandHandler
    public GameAggregate(CreateGameCommand command) {
        boolean isWhitePlayer = isWhitePlayer(command.getPieceColor());

        AggregateLifecycle.apply(new GameCreatedEvent(
                command.getGameId(), isWhitePlayer ? command.getPlayerId() : null,
                !isWhitePlayer ? command.getPlayerId() : null,
                command.getInvitePlayerId(), command.getMinutes(), command.getIncrementSeconds()));
    }

    @CommandHandler
    public void handle(AcceptGameInviteCommand command) {
        if (whitePlayerId != null && blackPlayerId != null) {
            throw new GameAlreadyAcceptedException();
        }

        if (!invitePlayerId.equals(command.getPlayerId())) {
            throw new IncorrectInvitePlayerException();
        }

        AggregateLifecycle.apply(new GameInvitationAcceptedEvent(command.getGameId(), command.getPlayerId(), AggregateLifecycle.getVersion() + 1));
    }

    @CommandHandler
    public void handle(DeclineGameInviteCommand command) {
        if (gameCanceled) {
            throw new GameAlreadyDeclinedException();
        }
        else if (!invitePlayerId.equals(command.getPlayerId())) {
            throw new IncorrectInvitePlayerException();
        }

        AggregateLifecycle.apply(new GameInvitationDeclinedEvent(command.getGameId(), command.getPlayerId()));
    }

    @CommandHandler
    public void handle(PairPlayerWithGameCommand command) {
        if (whitePlayerId != null && blackPlayerId != null) {
            throw new GameAlreadyPairedException();
        }

        AggregateLifecycle.apply(new GamePairedEvent(
                command.getGameId(), command.getPlayerId(), command.getRequestId(), AggregateLifecycle.getVersion() + 1));
    }

    @CommandHandler
    public void handle(AssociateGameWithBoardCommand command) {
        AggregateLifecycle.apply(new BoardAssociatedEvent(
                command.getGameId(), command.getBoardId(), whitePlayerId, blackPlayerId, AggregateLifecycle.getVersion() + 1));
    }

    @CommandHandler
    public void handle(PlayerMoveCommand command) {
        if (result != null) {
            throw new GameIsFinishedException();
        }

        if (boardId == null) {
            throw new BoardIsNotReadyException();
        }

        if (!command.getPlayerId().equals(whitePlayerId) && !command.getPlayerId().equals(blackPlayerId)) {
            throw new ItsNotYourGameException();
        }

        if (command.getPlayerId().equals(whitePlayerId)) {
            AggregateLifecycle.apply(new PlayerMovedEvent(
                    boardId, PlayerColor.WHITE, command.getMove()));
        }
        else {
            AggregateLifecycle.apply(new PlayerMovedEvent(
                    boardId, PlayerColor.BLACK, command.getMove()));
        }
    }

    @CommandHandler
    public void handle(FinishGameCommand command) {
        AggregateLifecycle.apply(new GameFinishedEvent(
                command.getGameId(), command.getResult(), whitePlayerId, blackPlayerId, AggregateLifecycle.getVersion() + 1));
    }

    @CommandHandler
    public void handle(ResignGameCommand command) {
        if (boardId == null) {
            throw new BoardIsNotReadyException();
        }

        if (whitePlayerId.equals(command.getPlayerId())) {
            AggregateLifecycle.apply(new GameResignedEvent(
                    command.getGameId(), boardId, "0-1", PlayerColor.BLACK));
        }
        else if (blackPlayerId.equals(command.getPlayerId())) {
            AggregateLifecycle.apply(new GameResignedEvent(
                    command.getGameId(), boardId, "1-0", PlayerColor.WHITE));
        }
        else {
            throw new ItsNotYourGameException();
        }
    }

    @CommandHandler
    public void handle(DrawGameCommand command) {
        if (boardId == null) {
            throw new BoardIsNotReadyException();
        }

        if (command.getPlayerId().equals(whitePlayerId)) {
            AggregateLifecycle.apply(new GameDrawEvent(boardId, PlayerColor.WHITE));
        }
        else if (command.getPlayerId().equals(blackPlayerId)) {
            AggregateLifecycle.apply(new GameDrawEvent(boardId, PlayerColor.BLACK));
        }
        else {
            throw new ItsNotYourGameException();
        }
    }

    @CommandHandler
    public void handle(CancelGameCommand command) {
        if (gameCanceled) {
            throw new GameIsCanceledException();
        }

        AggregateLifecycle.apply(new GameCanceledEvent(command.getGameId()));
    }

    private boolean isWhitePlayer(CreateGameCommand.PieceColor pieceColor) {
        if (pieceColor == CreateGameCommand.PieceColor.RANDOM) {
            return new Random().nextBoolean();
        }

        return pieceColor == CreateGameCommand.PieceColor.WHITE;
    }

    @EventSourcingHandler
    public void on(GameCreatedEvent event) {
        gameId = event.getGameId();
        whitePlayerId = event.getWhitePlayerId();
        blackPlayerId = event.getBlackPlayerId();
        minutes = event.getMinutes();
        incrementSeconds = event.getIncrementSeconds();
        invitePlayerId = event.getInvitePlayerId();
    }

    @EventSourcingHandler
    public void on(GameInvitationAcceptedEvent event) {
        if (whitePlayerId == null) {
            whitePlayerId = event.getPlayerId();
        }
        else {
            blackPlayerId = event.getPlayerId();
        }

        AggregateLifecycle.apply(new GameReadyEvent(gameId, minutes, incrementSeconds));
    }

    @EventSourcingHandler
    public void on(GameInvitationDeclinedEvent event) {
        gameCanceled = true;

        AggregateLifecycle.markDeleted();
    }

    @EventSourcingHandler
    public void on(GameCanceledEvent event) {
        gameCanceled = true;

        AggregateLifecycle.markDeleted();
    }

    @EventSourcingHandler
    public void on(GamePairedEvent event) {
        if (whitePlayerId == null) {
            whitePlayerId = event.getPlayerId();
        }
        else {
            blackPlayerId = event.getPlayerId();
        }

        AggregateLifecycle.apply(new GameReadyEvent(gameId, minutes, incrementSeconds));
    }

    @EventSourcingHandler
    public void on(BoardAssociatedEvent event) {
        boardId = event.getBoardId();
    }

    @EventSourcingHandler
    public void on(GameFinishedEvent event) {
        result = event.getResult();
    }

    @EventSourcingHandler
    public void on(GameResignedEvent event) {
        result = event.getResult();
    }
}