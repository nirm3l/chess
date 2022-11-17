package is.symphony.chess.game.core.queries;

import java.util.UUID;

public class LiveUpdatesQuery {

    private UUID gameId;

    public LiveUpdatesQuery() { }
    public LiveUpdatesQuery(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
