package is.symphony.chess.player.core.events;

import java.util.UUID;

public class PlayerRatingUpdatedEvent {

    private UUID playerId;

    private Integer ratingDelta;

    private Long version;

    private boolean reverted = false;

    public PlayerRatingUpdatedEvent() { }

    public PlayerRatingUpdatedEvent(final UUID playerId, final Integer ratingDelta, final Long version) {
        this.playerId = playerId;
        this.ratingDelta = ratingDelta;
        this.version = version;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public Integer getRatingDelta() {
        return ratingDelta;
    }

    public void setRatingDelta(final Integer ratingDelta) {
        this.ratingDelta = ratingDelta;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public boolean isReverted() {
        return reverted;
    }

    public void setReverted(final boolean reverted) {
        this.reverted = reverted;
    }
}
