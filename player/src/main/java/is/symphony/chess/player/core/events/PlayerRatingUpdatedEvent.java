package is.symphony.chess.player.core.events;

import java.util.UUID;

public class PlayerRatingUpdatedEvent {

    private UUID playerId;

    private Integer rating;

    public PlayerRatingUpdatedEvent() { }

    public PlayerRatingUpdatedEvent(final UUID playerId, final Integer rating) {
        this.playerId = playerId;
        this.rating = rating;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }
}
