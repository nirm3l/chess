package is.symphony.chess.board.core.exceptions;

public class BoardIsCanceledException extends IllegalStateException {
    public BoardIsCanceledException() {
        super("Board is canceled.");
    }
}