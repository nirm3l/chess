package is.symphony.chess.core.exceptions;

public class IncorrectInvitePlayerException extends IllegalStateException {
    public IncorrectInvitePlayerException() {
        super("You are not invited for this game.");
    }
}