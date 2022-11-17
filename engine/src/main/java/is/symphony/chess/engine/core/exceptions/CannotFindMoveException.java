package is.symphony.chess.engine.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CannotFindMoveException extends ResponseStatusException {
    public CannotFindMoveException() {
        super(HttpStatus.NOT_FOUND, "Cannot find move.");
    }
}