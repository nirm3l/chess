package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GameIsFinishedException extends ResponseStatusException {
    public GameIsFinishedException() {
        super(HttpStatus.CONFLICT, "Game is finished.");
    }
}