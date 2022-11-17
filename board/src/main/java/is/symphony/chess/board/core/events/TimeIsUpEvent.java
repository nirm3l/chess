package is.symphony.chess.board.core.events;

import is.symphony.chess.board.core.models.PlayerColor;

import java.util.UUID;

public class TimeIsUpEvent {

    private UUID boardId;

    private PlayerColor playerColor;

    public TimeIsUpEvent() { }

    public TimeIsUpEvent(final UUID boardId, final PlayerColor playerColor) {
        this.boardId = boardId;
        this.playerColor = playerColor;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(final PlayerColor playerColor) {
        this.playerColor = playerColor;
    }
}
