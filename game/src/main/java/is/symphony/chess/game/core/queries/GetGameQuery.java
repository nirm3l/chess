package is.symphony.chess.game.core.queries;

import java.util.UUID;

public class GetGameQuery {

    private UUID gameId;

    public GetGameQuery() {}

    public GetGameQuery(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
