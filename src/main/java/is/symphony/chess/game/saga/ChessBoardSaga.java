package is.symphony.chess.game.saga;

import is.symphony.chess.board.core.commands.CreateBoardCommand;
import is.symphony.chess.board.core.events.BoardCreatedEvent;
import is.symphony.chess.game.core.events.GameReadyEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

import java.util.UUID;

@Saga
public class CreateBoardGameSaga {

    public static final String GAME_ID_ASSOCIATION = "gameId";

    private final transient CommandGateway commandGateway;

    public CreateBoardGameSaga(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GameReadyEvent gameReadyEvent) {
        commandGateway.<UUID>send(new CreateBoardCommand(
                gameReadyEvent.getGameId(), gameReadyEvent.getMinutes(),
                        gameReadyEvent.getIncrementSeconds()));
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    @EndSaga
    public void handle(BoardCreatedEvent boardCreatedEvent) {}
}
