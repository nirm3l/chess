package is.symphony.chess.game.core.events;

import java.util.UUID;

public class RetryUpdatePlayerRatingsEvent {

    private UUID gameId;

    public RetryUpdatePlayerRatingsEvent() { }

    public RetryUpdatePlayerRatingsEvent(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
