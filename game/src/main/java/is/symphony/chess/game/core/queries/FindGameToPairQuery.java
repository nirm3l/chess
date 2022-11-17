package is.symphony.chess.game.core.queries;

import java.util.UUID;

public class FindGameToPairQuery {

    private Integer minutes;

    private Integer incrementSeconds;

    private UUID playerId;

    public FindGameToPairQuery() { }

    public FindGameToPairQuery(final UUID playerId, final Integer minutes, final Integer incrementSeconds) {
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
        this.playerId = playerId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }
}
