package is.symphony.chess.board.core.exceptions;

public class ItsNotYourTurnException extends IllegalStateException {
    public ItsNotYourTurnException() {
        super("It's not your turn.");
    }
}