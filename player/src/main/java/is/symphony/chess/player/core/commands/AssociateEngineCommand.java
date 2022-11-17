package is.symphony.chess.player.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class AssociateEngineCommand {

    @TargetAggregateIdentifier
    private UUID playerId;

    private UUID engineId;

    public AssociateEngineCommand() {
    }

    public AssociateEngineCommand(final UUID playerId, final UUID engineId) {
        this.playerId = playerId;
        this.engineId = engineId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getEngineId() {
        return engineId;
    }

    public void setEngineId(final UUID engineId) {
        this.engineId = engineId;
    }
}
