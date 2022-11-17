package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GameIsCanceledException extends ResponseStatusException {
    public GameIsCanceledException() {
        super(HttpStatus.CONFLICT, "Game is canceled.");
    }
}