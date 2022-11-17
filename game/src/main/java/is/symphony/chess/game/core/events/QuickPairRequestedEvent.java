package is.symphony.chess.game.core.events;

import java.util.UUID;

public class QuickPairRequestedEvent {

    private UUID requestId;

    private UUID playerId;

    private Integer minutes;

    private Integer incrementSeconds;

    public QuickPairRequestedEvent() { }
    public QuickPairRequestedEvent(final UUID requestId, final UUID playerId, final Integer minutes, final Integer incrementSeconds) {
        this.requestId = requestId;
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

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }
}
