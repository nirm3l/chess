package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GamePairedEvent {

    private UUID gameId;

    private UUID playerId;

    private UUID requestId;

    public GamePairedEvent() { }

    public GamePairedEvent(final UUID gameId, final UUID playerId, final UUID requestId) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.requestId = requestId;
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

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }
}
