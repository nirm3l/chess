package is.symphony.chess.board.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class BoardException extends ResponseStatusException {
    private UUID boardId;

    public BoardException() {
        super(HttpStatus.CONFLICT);
    }

    public BoardException(final UUID boardId, final String message) {
        super(HttpStatus.CONFLICT, message);
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}