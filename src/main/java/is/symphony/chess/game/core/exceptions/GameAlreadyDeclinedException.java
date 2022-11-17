package is.symphony.chess.core.exceptions;

public class GameAlreadyDeclinedException extends IllegalStateException {
    public GameAlreadyDeclinedException() {
        super("Game already accepter.");
    }
}