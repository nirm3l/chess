package is.symphony.chess.player.saga;

import is.symphony.chess.FailedSagasHandler;
import is.symphony.chess.player.saga.PlayerEngineSaga;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FailedSagasHandlerTask {

    private final FailedSagasHandler<PlayerEngineSaga> playerEngineSagaFailedSagasHandler;

    private final CommandGateway commandGateway;

    public FailedSagasHandlerTask(final FailedSagasHandler<PlayerEngineSaga> playerEngineSagaFailedSagasHandler,
                                  final CommandGateway commandGateway) {
        this.playerEngineSagaFailedSagasHandler = playerEngineSagaFailedSagasHandler;
        this.commandGateway = commandGateway;
    }

    @Scheduled(fixedRate = 10000)
    public void start() {
        playerEngineSagaFailedSagasHandler.execute((saga) -> saga.setCommandGateway(commandGateway));
    }
}
