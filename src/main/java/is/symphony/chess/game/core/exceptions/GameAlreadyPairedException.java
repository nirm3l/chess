package is.symphony.chess.core.exceptions;

public class GameAlreadyPairedException extends IllegalStateException {
    public GameAlreadyPairedException() {
        super("Game already paired.");
    }
}