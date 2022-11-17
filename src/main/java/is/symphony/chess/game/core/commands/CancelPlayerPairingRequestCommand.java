package is.symphony.chess.game.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CancelPlayerPairingCommand {

    @TargetAggregateIdentifier
    private UUID requestId;

    public CancelPlayerPairingCommand() { }
    public CancelPlayerPairingCommand(final UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }
}
