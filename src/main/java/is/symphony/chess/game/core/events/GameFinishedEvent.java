package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GamePairedEvent {

    private UUID gameId;

    private UUID playerId;

    public GamePairedEvent() { }
    public GamePairedEvent(final UUID gameId, final UUID playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }
}
