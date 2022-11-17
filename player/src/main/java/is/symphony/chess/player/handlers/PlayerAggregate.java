package is.symphony.chess.player.handlers;

import is.symphony.chess.player.core.commands.AssociateEngineCommand;
import is.symphony.chess.player.core.commands.CheckPlayerExistenceCommand;
import is.symphony.chess.player.core.commands.RegisterPlayerCommand;
import is.symphony.chess.player.core.commands.UpdateRatingCommand;
import is.symphony.chess.player.core.events.PlayerEngineAssociatedEvent;
import is.symphony.chess.player.core.events.PlayerRatingUpdatedEvent;
import is.symphony.chess.player.core.events.PlayerRegisteredEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "chessAggregateSnapshotTriggerDefinition")
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
    public void handle(UpdateRatingCommand command) {
        AggregateLifecycle.apply(new PlayerRatingUpdatedEvent(command.getPlayerId(), command.getRating()));
    }

    @CommandHandler
    public void handle(AssociateEngineCommand command) {
        AggregateLifecycle.apply(new PlayerEngineAssociatedEvent(command.getPlayerId(), command.getEngineId()));
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
        rating = event.getRating();
    }
}