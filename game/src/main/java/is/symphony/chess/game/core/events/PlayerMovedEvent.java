package is.symphony.chess.game.core.events;

import is.symphony.chess.game.core.models.PlayerColor;

import java.util.UUID;

public class PlayerMovedEvent {

    private UUID boardId;

    private PlayerColor playerColor;

    private String move;

    public PlayerMovedEvent() { }

    public PlayerMovedEvent(final UUID boardId, final PlayerColor playerColor, final String move) {
        this.boardId = boardId;
        this.playerColor = playerColor;
        this.move = move;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public String getMove() {
        return move;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public void setPlayerColor(final PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public void setMove(final String move) {
        this.move = move;
    }
}
