package is.symphony.chess.player.core.queries;

import java.util.UUID;

public class GetPlayerQuery {

    private UUID playerId;

    public GetPlayerQuery() {}

    public GetPlayerQuery(final UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }
}
