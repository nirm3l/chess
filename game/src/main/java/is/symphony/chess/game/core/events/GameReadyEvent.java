package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GameReadyEvent {

    private UUID gameId;

    private Integer minutes;

    private Integer incrementSeconds;

    private boolean retry;

    public GameReadyEvent() { }
    public GameReadyEvent(final UUID gameId, final Integer minutes, final Integer incrementSeconds) {
        this.gameId = gameId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getGameId() {
        return gameId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(final boolean retry) {
        this.retry = retry;
    }
}
