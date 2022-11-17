package is.symphony.chess.player.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CheckPlayerExistenceCommand {

    @TargetAggregateIdentifier
    private UUID playerId;

    public CheckPlayerExistenceCommand() {
    }

    public CheckPlayerExistenceCommand(final UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }
}
