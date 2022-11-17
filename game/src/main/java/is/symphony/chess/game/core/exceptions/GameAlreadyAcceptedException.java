package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GameAlreadyAcceptedException extends ResponseStatusException {
    public GameAlreadyAcceptedException() {
        super(HttpStatus.CONFLICT, "Game already accepted.");
    }
}