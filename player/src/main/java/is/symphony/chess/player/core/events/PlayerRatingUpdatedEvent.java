package is.symphony.chess.player.core.events;

import java.util.UUID;

public class PlayerRatingUpdatedEvent {

    private UUID playerId;

    private UUID gameId;

    private Integer ratingDelta;

    private Long version;

    private boolean reverted = false;

    public PlayerRatingUpdatedEvent() { }

    public PlayerRatingUpdatedEvent(final UUID playerId, final UUID gameId, final Integer ratingDelta, final Long version, final boolean reverted) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.ratingDelta = ratingDelta;
        this.version = version;
        this.reverted = reverted;
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

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
