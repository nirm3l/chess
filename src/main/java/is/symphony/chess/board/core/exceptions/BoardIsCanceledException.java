package is.symphony.chess.game.core.exceptions;

public class GameIsCanceledException extends IllegalStateException {
    public GameIsCanceledException() {
        super("Game is canceled.");
    }
}