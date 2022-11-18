package is.symphony.chess.game.saga;

import is.symphony.chess.FailedSagasHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FailedSagasHandlerTask {

    private final FailedSagasHandler<ChessBoardSaga> failedSagasHandler;

    private final CommandGateway commandGateway;

    public FailedSagasHandlerTask(final FailedSagasHandler<ChessBoardSaga> failedSagasHandler,
                                  final CommandGateway commandGateway) {
        this.failedSagasHandler = failedSagasHandler;
        this.commandGateway = commandGateway;
    }

    @Scheduled(fixedRate = 10000)
    public void start() {
        failedSagasHandler.execute(saga -> saga.setCommandGateway(commandGateway));
    }
}
