package is.symphony.chess.board.core.events;

import java.util.UUID;

public class BoardGameFinishedEvent {

    private UUID boardId;

    private String result;

    public BoardGameFinishedEvent() { }

    public BoardGameFinishedEvent(final UUID boardId, final String result) {
        this.boardId = boardId;
        this.result = result;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(final String result) {
        this.result = result;
    }
}
