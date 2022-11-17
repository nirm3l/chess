package is.symphony.chess.core.queries;

import java.util.UUID;

public class GetGameQuery {

    private final UUID gameId;

    public GetGameQuery(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }
}
