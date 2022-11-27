package is.symphony.chess.player.core.events;

import java.util.UUID;

public class PlayerRatingUpdateFailedEvent {

    private UUID playerId;

    private UUID gameId;

    public PlayerRatingUpdateFailedEvent() { }

    public PlayerRatingUpdateFailedEvent(final UUID playerId, final UUID gameId) {
        this.playerId = playerId;
        this.gameId = gameId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
