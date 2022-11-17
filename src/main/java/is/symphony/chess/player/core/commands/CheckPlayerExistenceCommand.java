package is.symphony.chess.player.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CheckPlayerExistanceCommand {

    @TargetAggregateIdentifier
    private UUID playerId;

    public CheckPlayerExistanceCommand() {
    }

    public CheckPlayerExistanceCommand(final UUID playerId) {
        this.playerId = playerId;
    }
}
