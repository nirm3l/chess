package is.symphony.chess.core.exceptions;

public class GameAlreadyAcceptedException extends IllegalStateException {
    public GameAlreadyAcceptedException() {
        super("Game already accepter.");
    }
}