package is.symphony.chess.board.core.events;

import java.util.UUID;

public class BoardCanceledEvent {

    private UUID boardId;

    public BoardCanceledEvent() { }

    public BoardCanceledEvent(final UUID boardId) {
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}
