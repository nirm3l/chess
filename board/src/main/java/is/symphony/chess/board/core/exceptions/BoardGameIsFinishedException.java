package is.symphony.chess.board.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BoardGameIsFinishedException extends ResponseStatusException {
    public BoardGameIsFinishedException() {
        super(HttpStatus.CONFLICT, "Board game is finished.");
    }
}