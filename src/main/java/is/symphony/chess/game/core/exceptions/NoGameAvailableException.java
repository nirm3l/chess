package is.symphony.chess.core.exceptions;

public class NoGameAvailableException extends IllegalStateException {
    public NoGameAvailableException() {
        super("There is no game available.");
    }
}