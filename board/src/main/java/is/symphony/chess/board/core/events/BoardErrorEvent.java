package is.symphony.chess.board.core.events;

import java.util.UUID;

public class BoardErrorEvent {

    private UUID boardId;

    private String message;

    public BoardErrorEvent() { }

    public BoardErrorEvent(final UUID boardId, final String message) {
        this.boardId = boardId;
        this.message = message;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
