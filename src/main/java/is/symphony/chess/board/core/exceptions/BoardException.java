package is.symphony.chess.board.core.exceptions;

import java.util.UUID;

public class IllegalMoveException extends IllegalStateException {
    private final UUID boardId;
    public IllegalMoveException(final UUID boardId) {
        super("Illegal move.");
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }
}