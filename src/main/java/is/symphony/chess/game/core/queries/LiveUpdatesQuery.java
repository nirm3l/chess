package is.symphony.chess.board.core.queries;

import java.util.UUID;

public class LiveUpdatesQuery {

    private UUID boardId;

    public LiveUpdatesQuery() { }
    public LiveUpdatesQuery(final UUID boardId) {
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}
