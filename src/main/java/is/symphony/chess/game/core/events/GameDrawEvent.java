package is.symphony.chess.game.core.events;

import is.symphony.chess.game.core.models.PlayerColor;

import java.util.UUID;

public class GameResignedEvent {

    private UUID gameId;

    private String result;

    private PlayerColor playerColor;

    public GameResignedEvent() { }

    public GameResignedEvent(final UUID gameId, final String result, final PlayerColor playerColor) {
        this.gameId = gameId;
        this.result = result;
        this.playerColor = playerColor;
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

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(final PlayerColor playerColor) {
        this.playerColor = playerColor;
    }
}
