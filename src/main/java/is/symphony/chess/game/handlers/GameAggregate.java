package is.symphony.chess.handlers;

import is.symphony.chess.core.commands.AcceptGameInviteCommand;
import is.symphony.chess.core.commands.CreateGameCommand;
import is.symphony.chess.core.commands.DeclineGameInviteCommand;
import is.symphony.chess.core.commands.PairPlayerWithGameCommand;
import is.symphony.chess.core.events.GameInvitationAcceptedEvent;
import is.symphony.chess.core.events.GameCreatedEvent;
import is.symphony.chess.core.events.GameInvitationDeclinedEvent;
import is.symphony.chess.core.events.GamePairedEvent;
import is.symphony.chess.core.exceptions.GameAlreadyAcceptedException;
import is.symphony.chess.core.exceptions.GameAlreadyDeclinedException;
import is.symphony.chess.core.exceptions.GameAlreadyPairedException;
import is.symphony.chess.core.exceptions.IncorrectInvitePlayerException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Random;
import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "chessAggregateSnapshotTriggerDefinition")
public class GameAggregate {
    @AggregateIdentifier
    private UUID gameId;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    private UUID invitePlayerId;

    private Integer minutes;

    private Integer incrementSeconds;

    private boolean gameCanceled;

    @AggregateMember
    private ChessBoardAggregate chessBoard;

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

        if (!command.getPlayerId().equals(invitePlayerId)) {
            throw new IncorrectInvitePlayerException();
        }

        AggregateLifecycle.apply(new GameInvitationAcceptedEvent(command.getGameId(), command.getPlayerId()));
    }

    @CommandHandler
    public void handle(DeclineGameInviteCommand command) {
        if (gameCanceled) {
            throw new GameAlreadyDeclinedException();
        }

        AggregateLifecycle.apply(new GameInvitationDeclinedEvent(command.getGameId(), command.getPlayerId()));
    }

    @CommandHandler
    public void handle(PairPlayerWithGameCommand command) {
        if (whitePlayerId != null && blackPlayerId != null) {
            throw new GameAlreadyPairedException();
        }

        AggregateLifecycle.apply(new GamePairedEvent(command.getGameId(), command.getPlayerId()));
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
    }

    @EventSourcingHandler
    public void on(GameInvitationDeclinedEvent event) {
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
    }

    protected GameAggregate() { }
}