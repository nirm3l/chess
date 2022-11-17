package is.symphony.chess.board.core.queries;

import java.util.UUID;

public class MoveUpdatesQuery {

    private UUID boardId;

    public MoveUpdatesQuery() { }
    public MoveUpdatesQuery(final UUID boardId) {
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}
