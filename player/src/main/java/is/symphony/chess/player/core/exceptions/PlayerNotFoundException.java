package is.symphony.chess.player.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PlayerNotFoundException extends ResponseStatusException {
    public PlayerNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Player not found.");
    }
}