package is.symphony.chess.game.saga;

import is.symphony.chess.board.core.commands.CreateBoardCommand;
import is.symphony.chess.board.core.commands.PlayMoveCommand;
import is.symphony.chess.board.core.events.BoardCreatedEvent;
import is.symphony.chess.board.core.events.BoardGameFinishedEvent;
import is.symphony.chess.board.core.models.PlayerColor;
import is.symphony.chess.game.core.commands.AssociateGameWithBoardCommand;
import is.symphony.chess.game.core.commands.FinishGameCommand;
import is.symphony.chess.game.core.events.BoardAssociatedEvent;
import is.symphony.chess.game.core.events.GameFinishedEvent;
import is.symphony.chess.game.core.events.GameReadyEvent;
import is.symphony.chess.game.core.events.PlayerMovedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
public class ChessBoardSaga {

    public static final String GAME_ID_ASSOCIATION = "gameId";

    public static final String BOARD_ID_ASSOCIATION = "boardId";

    private transient CommandGateway commandGateway;

    private UUID gameId;

    private UUID boardId;

    @Autowired
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameReadyEvent gameReadyEvent) {
        gameId = gameReadyEvent.getGameId();

        if (gameReadyEvent.getBoardId() == null) {
            boardId = UUID.randomUUID();

            SagaLifecycle.associateWith(BOARD_ID_ASSOCIATION, boardId.toString());

            commandGateway.<UUID>send(new CreateBoardCommand(boardId, gameReadyEvent.getMinutes(),
                    gameReadyEvent.getIncrementSeconds()));
        }
        else {
            boardId = gameReadyEvent.getBoardId();
            SagaLifecycle.associateWith(BOARD_ID_ASSOCIATION, boardId.toString());
        }
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(BoardCreatedEvent boardCreatedEvent) {
        commandGateway.send(new AssociateGameWithBoardCommand(gameId, boardId));
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(BoardGameFinishedEvent boardGameFinishedEvent) {
        commandGateway.send(new FinishGameCommand(gameId, boardGameFinishedEvent.getResult()));
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(BoardAssociatedEvent boardAssociatedEvent) {
        boardId = boardAssociatedEvent.getBoardId();
    }

    @SagaEventHandler(associationProperty = BOARD_ID_ASSOCIATION)
    public void handle(PlayerMovedEvent playerMovedEvent) {
        commandGateway.send(
                new PlayMoveCommand(playerMovedEvent.getBoardId(),
                        PlayerColor.valueOf(playerMovedEvent.getPlayerColor().toString()),
                        playerMovedEvent.getMove()));
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameFinishedEvent gameFinishedEvent) {
        SagaLifecycle.end();
    }
}
