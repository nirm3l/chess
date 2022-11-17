package is.symphony.chess.player.core.events;

import java.util.UUID;

public class PlayerEngineAssociatedEvent {

    private UUID playerId;

    private UUID engineId;

    public PlayerEngineAssociatedEvent() { }

    public PlayerEngineAssociatedEvent(final UUID playerId, final UUID engineId) {
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
