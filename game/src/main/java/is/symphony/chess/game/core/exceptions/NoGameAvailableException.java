package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoGameAvailableException extends ResponseStatusException {
    public NoGameAvailableException() {
        super(HttpStatus.NOT_FOUND, "There is no game available.");
    }
}