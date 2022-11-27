package is.symphony.chess.player.core.events;

import java.util.UUID;

public class PlayerRatingUpdateFailedEvent {

    private UUID playerId;

    public PlayerRatingUpdateFailedEvent() { }

    public PlayerRatingUpdateFailedEvent(final UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }
}
