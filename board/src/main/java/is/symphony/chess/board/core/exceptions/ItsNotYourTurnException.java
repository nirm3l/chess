package is.symphony.chess.board.core.exceptions;

import java.util.UUID;

public class ItsNotYourTurnException extends BoardException {

    public ItsNotYourTurnException() { }
    public ItsNotYourTurnException(UUID boardId) {
        super(boardId, "It's not your turn.");
    }
}