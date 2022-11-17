package is.symphony.chess.game.core.events;

import java.util.UUID;

public class PlayerPairingCanceledEvent {

    private UUID requestId;

    public PlayerPairingCanceledEvent() { }
    public PlayerPairingCanceledEvent(final UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }
}
