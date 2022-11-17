package is.symphony.chess.board.core.events;

import java.util.UUID;

public class CancelBoardEvent {

    private UUID boardId;

    public CancelBoardEvent() { }

    public CancelBoardEvent(final UUID boardId) {
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}
