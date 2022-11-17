package is.symphony.chess.core.events;

import java.util.UUID;

public class QuickPairRequestedEvent {

    private final UUID playerId;

    private final Integer minutes;

    private final Integer incrementSeconds;

    public QuickPairRequestedEvent(final UUID playerId, final Integer minutes, final Integer incrementSeconds) {
        this.playerId = playerId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }
}
