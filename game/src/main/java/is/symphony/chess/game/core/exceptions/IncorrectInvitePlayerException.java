package is.symphony.chess.game.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IncorrectInvitePlayerException extends ResponseStatusException {
    public IncorrectInvitePlayerException() {
        super(HttpStatus.UNAUTHORIZED, "You are not invited for this game.");
    }
}