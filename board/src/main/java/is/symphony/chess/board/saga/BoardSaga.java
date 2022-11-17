package is.symphony.chess.board.saga;

import is.symphony.chess.board.core.commands.CancelBoardCommand;
import is.symphony.chess.board.core.commands.FinishBoardGameCommand;
import is.symphony.chess.board.core.events.*;
import is.symphony.chess.board.core.models.BoardMove;
import is.symphony.chess.board.core.models.PlayerColor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.UUID;

@Saga
public class BoardSaga {

    public static final String BOARD_ID_ASSOCIATION = "boardId";

    private transient CommandGateway commandGateway;

    private transient EventScheduler scheduler;

    private UUID boardId;

    private ScheduleToken cancelBoardToken;

    private ScheduleToken clockTimeoutToken;

    private Long whiteClock;

    private Long blackClock;

    @Autowired
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setScheduler(final EventScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(BoardCreatedEvent boardCreatedEvent) {
        boardId = boardCreatedEvent.getBoardId();

        cancelBoardToken = scheduler.schedule(Duration.ofMinutes(1), new CancelBoardEvent(boardId));
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(MovePlayedEvent gameReadyEvent) {
        final BoardMove boardMove = gameReadyEvent.getBoardMove();

        if (cancelBoardToken != null && boardMove.getPlayerColor() == PlayerColor.BLACK) {
            scheduler.cancelSchedule(cancelBoardToken);
            cancelBoardToken = null;
        }

        if (clockTimeoutToken != null) {
            scheduler.cancelSchedule(clockTimeoutToken);
            clockTimeoutToken = null;
        }

        if (boardMove.getPlayerColor() == PlayerColor.WHITE) {
            if (blackClock != null) {
                clockTimeoutToken = scheduler.schedule(Duration.ofMillis(blackClock), new TimeIsUpEvent(boardId, PlayerColor.BLACK));
            }

            whiteClock = boardMove.getClock();
        }
        else {
            if (whiteClock != null) {
                clockTimeoutToken = scheduler.schedule(Duration.ofMillis(whiteClock), new TimeIsUpEvent(boardId, PlayerColor.WHITE));
            }

            blackClock = boardMove.getClock();
        }
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(CancelBoardEvent boardCanceledEvent) {
        commandGateway.sendAndWait(new CancelBoardCommand(boardId));
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(BoardCanceledEvent boardCanceledEvent) {
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(TimeIsUpEvent timeIsUpEvent) {
        final FinishBoardGameCommand command = new FinishBoardGameCommand(
                boardId, getOppositePlayer(timeIsUpEvent.getPlayerColor()));
        command.setTimeIsUp(true);

        commandGateway.sendAndWait(command);
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(BoardGameFinishedEvent boardGameFinishedEvent) {
        if (clockTimeoutToken != null) {
            scheduler.cancelSchedule(clockTimeoutToken);
            clockTimeoutToken = null;
        }

        if (cancelBoardToken != null) {
            scheduler.cancelSchedule(cancelBoardToken);
            cancelBoardToken = null;
        }

        SagaLifecycle.end();
    }

    private PlayerColor getOppositePlayer(PlayerColor playerColor) {
        if (playerColor == PlayerColor.WHITE) {
            return PlayerColor.BLACK;
        }
        else {
            return PlayerColor.WHITE;
        }
    }
}
