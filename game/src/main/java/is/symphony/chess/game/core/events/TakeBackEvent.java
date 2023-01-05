package is.symphony.chess.game.core.events;

import is.symphony.chess.game.core.models.PlayerColor;

import java.util.UUID;

public class TakeBackEvent {

    private UUID boardId;

    private PlayerColor playerColor;

    public TakeBackEvent() { }

    public TakeBackEvent(final UUID boardId, final PlayerColor playerColor) {
        this.boardId = boardId;
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(final PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}
