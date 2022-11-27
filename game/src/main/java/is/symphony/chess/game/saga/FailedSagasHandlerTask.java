package is.symphony.chess.game.saga;

import is.symphony.chess.FailedSagasHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FailedSagasHandlerTask {

    private final FailedSagasHandler<ChessBoardSaga> failedSagasHandler;

    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    private final EventScheduler eventScheduler;

    public FailedSagasHandlerTask(final FailedSagasHandler<ChessBoardSaga> failedSagasHandler,
                                  final CommandGateway commandGateway, final QueryGateway queryGateway,
                                  final EventScheduler eventScheduler) {
        this.failedSagasHandler = failedSagasHandler;
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.eventScheduler = eventScheduler;
    }

    @Scheduled(fixedRate = 10000)
    public void start() {
        failedSagasHandler.execute(saga -> {
            saga.setCommandGateway(commandGateway);
            saga.setQueryGateway(queryGateway);
            saga.setEventScheduler(eventScheduler);
        });
    }
}
