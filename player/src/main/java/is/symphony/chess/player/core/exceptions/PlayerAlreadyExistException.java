package is.symphony.chess.player.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PlayerAlreadyExistException extends ResponseStatusException {
    public PlayerAlreadyExistException() {
        super(HttpStatus.CONFLICT, "Player already exist.");
    }
}