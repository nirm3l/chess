package is.symphony.chess.player.saga;

import is.symphony.chess.engine.core.commands.RegisterEngineCommand;
import is.symphony.chess.engine.core.events.EngineRegisteredEvent;
import is.symphony.chess.player.core.commands.AssociateEngineCommand;
import is.symphony.chess.player.core.events.PlayerEngineAssociatedEvent;
import is.symphony.chess.player.core.events.PlayerRegisteredEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
public class PlayerEngineSaga {

    public static final String PLAYER_ID_ASSOCIATION = "playerId";

    public static final String ENGINE_ID_ASSOCIATION = "engineId";

    private transient CommandGateway commandGateway;

    private UUID playerId;

    private UUID engineId;

    @Autowired
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = PLAYER_ID_ASSOCIATION)
    public void handle(PlayerRegisteredEvent playerRegisteredEvent) {
        playerId = playerRegisteredEvent.getPlayerId();

        if (!playerRegisteredEvent.isBot()) {
            SagaLifecycle.end();
        }

        if (engineId == null) {
            engineId = UUID.randomUUID();
            SagaLifecycle.associateWith(ENGINE_ID_ASSOCIATION, engineId.toString());
        }

        commandGateway.send(new RegisterEngineCommand(
                engineId, playerRegisteredEvent.getName(), playerRegisteredEvent.getLevel()));
    }

    @SagaEventHandler(associationProperty = ENGINE_ID_ASSOCIATION)
    public void handle(EngineRegisteredEvent gamePairedEvent) {
        final UUID engineId = gamePairedEvent.getEngineId();

        commandGateway.send(new AssociateEngineCommand(playerId, engineId));
    }

    @SagaEventHandler(associationProperty = PLAYER_ID_ASSOCIATION)
    public void handle(PlayerEngineAssociatedEvent playerPairingCanceledEvent) {
        SagaLifecycle.end();
    }
}
