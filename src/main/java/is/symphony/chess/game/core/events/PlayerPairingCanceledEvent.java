package is.symphony.chess.core.events;

import java.util.UUID;

public class PlayerPairingCanceledEvent {

    private final UUID playerId;

    public PlayerPairingCanceledEvent(final UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
