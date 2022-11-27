package is.symphony.chess.player.handlers;

import is.symphony.chess.player.core.commands.*;
import is.symphony.chess.player.core.events.PlayerEngineAssociatedEvent;
import is.symphony.chess.player.core.events.PlayerRatingUpdateFailedEvent;
import is.symphony.chess.player.core.events.PlayerRatingUpdatedEvent;
import is.symphony.chess.player.core.events.PlayerRegisteredEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.conflictresolution.ConflictResolver;
import org.axonframework.eventsourcing.conflictresolution.Conflicts;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.ConflictingModificationException;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "aggregateSnapshotTriggerDefinition")
public class PlayerAggregate {

    @AggregateIdentifier
    private UUID playerId;

    private UUID engineId;

    private String name;

    private String email;

    private boolean bot;

    private Integer level;

    private Integer rating = 1500;

    protected PlayerAggregate() { }


    @CommandHandler
    public PlayerAggregate(RegisterPlayerCommand command) {
        AggregateLifecycle.apply(new PlayerRegisteredEvent(
                command.getPlayerId(), command.getName(), command.getEmail(), command.isBot(), command.getLevel(), rating));
    }

    @CommandHandler
    public void handle(CheckPlayerExistenceCommand command) {
    }

    @CommandHandler
    public void handle(UpdateRatingCommand command, ConflictResolver conflictResolver) {
        try {
            conflictResolver.detectConflicts(Conflicts.payloadTypeOf(PlayerRatingUpdatedEvent.class));

            AggregateLifecycle.apply(new PlayerRatingUpdatedEvent(
                    command.getPlayerId(), command.getRatingDelta(), AggregateLifecycle.getVersion() + 1));
        }
        catch (ConflictingModificationException e) {
            AggregateLifecycle.apply(new PlayerRatingUpdateFailedEvent(command.getPlayerId()));
        }
    }

    @CommandHandler
    public void handle(RevertRatingCommand command) {
        PlayerRatingUpdatedEvent event = new PlayerRatingUpdatedEvent(
                command.getPlayerId(), command.getRatingDelta(), AggregateLifecycle.getVersion() + 1);
        event.setReverted(true);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(AssociateEngineCommand command) {
        AggregateLifecycle.apply(new PlayerEngineAssociatedEvent(command.getPlayerId(), command.getEngineId(), AggregateLifecycle.getVersion() + 1));
    }

    @EventSourcingHandler
    public void on(PlayerRegisteredEvent event) {
        playerId = event.getPlayerId();
        email = event.getEmail();
        name = event.getName();
        bot = event.isBot();
        level = event.getLevel();
    }

    @EventSourcingHandler
    public void on(PlayerEngineAssociatedEvent event) {
        engineId = event.getEngineId();
    }

    @EventSourcingHandler
    public void on(PlayerRatingUpdatedEvent event) {
        rating += event.getRatingDelta();
    }
}