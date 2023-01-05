package is.symphony.chess.board.core.events;

import java.util.UUID;

public class TakeBackAcceptedEvent {

    private UUID boardId;

    public TakeBackAcceptedEvent() { }

    public TakeBackAcceptedEvent(final UUID boardId) {
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}
