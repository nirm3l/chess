package is.symphony.chess.game.core.exceptions;

public class GameAlreadyAcceptedException extends IllegalStateException {
    public GameAlreadyAcceptedException() {
        super("Game already accepter.");
    }
}