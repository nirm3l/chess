package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItsNotYourGameException extends ResponseStatusException {
    public ItsNotYourGameException() {
        super(HttpStatus.UNAUTHORIZED, "It's not your game.");
    }
}