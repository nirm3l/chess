package is.symphony.chess.board.core.exceptions;

import java.util.UUID;

public class IllegalMoveException extends BoardException {

    public IllegalMoveException() {}
    public IllegalMoveException(final UUID boardId) {
        super(boardId, "Illegal move.");
    }
}