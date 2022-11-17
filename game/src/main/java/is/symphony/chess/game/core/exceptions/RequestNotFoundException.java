package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RequestNotFoundException extends ResponseStatusException {
    public RequestNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Request not found.");
    }
}