package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RequestIsCanceledException extends ResponseStatusException {
    public RequestIsCanceledException() {
        super(HttpStatus.CONFLICT, "Request is canceled.");
    }
}