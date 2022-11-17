package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GameAlreadyPairedException extends ResponseStatusException {
    public GameAlreadyPairedException() {
        super(HttpStatus.CONFLICT, "Game already paired.");
    }
}