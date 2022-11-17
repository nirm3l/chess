package is.symphony.chess.board.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BoardIsCanceledException extends ResponseStatusException {
    public BoardIsCanceledException() {
        super(HttpStatus.CONFLICT, "Board is canceled.");
    }
}