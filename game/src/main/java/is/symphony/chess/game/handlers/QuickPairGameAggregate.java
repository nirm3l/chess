package is.symphony.chess.game.handlers;

import is.symphony.chess.game.core.commands.CancelPlayerPairingRequestCommand;
import is.symphony.chess.game.core.commands.QuickPairRequestCommand;
import is.symphony.chess.game.core.events.PlayerPairingCanceledEvent;
import is.symphony.chess.game.core.events.QuickPairRequestedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "aggregateSnapshotTriggerDefinition")
public class QuickPairGameAggregate {

    @AggregateIdentifier
    private UUID requestId;

    private UUID playerId;

    private boolean canceled;

    private Integer minutes;

    private Integer incrementSeconds;

    protected QuickPairGameAggregate() { }

    @CommandHandler
    public QuickPairGameAggregate(QuickPairRequestCommand command) {
        AggregateLifecycle.apply(new QuickPairRequestedEvent(command.getRequestId(), command.getPlayerId(), command.getMinutes(), command.getIncrementSeconds()));
    }

    @CommandHandler
    public void handle(CancelPlayerPairingRequestCommand command) {
        AggregateLifecycle.apply(new PlayerPairingCanceledEvent(command.getRequestId()));
    }

    @EventSourcingHandler
    public void on(QuickPairRequestedEvent event) {
        requestId = event.getRequestId();
        canceled = false;
        playerId = event.getPlayerId();
        minutes = event.getMinutes();
        incrementSeconds = event.getIncrementSeconds();
    }

    @EventSourcingHandler
    public void on(PlayerPairingCanceledEvent event) {
        canceled = true;
    }
}