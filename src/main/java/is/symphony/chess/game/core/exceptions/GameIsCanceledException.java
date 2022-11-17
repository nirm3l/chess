package is.symphony.chess.game.core.exceptions;

public class GameIsFinishedException extends IllegalStateException {
    public GameIsFinishedException() {
        super("Game is finished.");
    }
}