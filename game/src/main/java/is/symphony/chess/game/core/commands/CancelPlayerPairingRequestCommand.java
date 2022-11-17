package is.symphony.chess.game.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CancelPlayerPairingRequestCommand {

    @TargetAggregateIdentifier
    private UUID requestId;

    public CancelPlayerPairingRequestCommand() { }
    public CancelPlayerPairingRequestCommand(final UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }
}
