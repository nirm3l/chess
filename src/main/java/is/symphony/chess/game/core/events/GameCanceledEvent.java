package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GameFinishedEvent {

    private UUID gameId;

    private String result;

    public GameFinishedEvent() { }
    public GameFinishedEvent(final UUID gameId, final String result) {
        this.gameId = gameId;
        this.result = result;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(final String result) {
        this.result = result;
    }
}
