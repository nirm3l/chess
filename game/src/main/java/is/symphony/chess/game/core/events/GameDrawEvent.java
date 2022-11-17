package is.symphony.chess.game.core.events;

import is.symphony.chess.game.core.models.PlayerColor;

import java.util.UUID;

public class GameDrawEvent {

    private UUID gameId;

    private PlayerColor playerColor;

    public GameDrawEvent() { }

    public GameDrawEvent(final UUID gameId, final PlayerColor playerColor) {
        this.gameId = gameId;
        this.playerColor = playerColor;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(final PlayerColor playerColor) {
        this.playerColor = playerColor;
    }
}
