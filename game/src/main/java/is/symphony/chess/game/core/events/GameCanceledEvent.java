package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GameCanceledEvent {

    private UUID gameId;

    public GameCanceledEvent() { }
    public GameCanceledEvent(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
