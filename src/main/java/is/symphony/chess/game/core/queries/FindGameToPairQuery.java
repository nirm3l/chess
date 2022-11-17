package is.symphony.chess.core.queries;

import java.util.UUID;

public class FindGameToPairQuery {

    private final Integer minutes;

    private final Integer incrementSeconds;

    private final UUID playerId;

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
}
