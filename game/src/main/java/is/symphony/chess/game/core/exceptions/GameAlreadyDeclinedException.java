package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GameAlreadyDeclinedException extends ResponseStatusException {
    public GameAlreadyDeclinedException() {
        super(HttpStatus.CONFLICT, "Game already declined.");
    }
}