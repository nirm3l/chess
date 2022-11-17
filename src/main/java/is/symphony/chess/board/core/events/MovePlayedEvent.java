package is.symphony.chess.board.core.events;

import java.util.UUID;

public class BoardCreatedEvent {

    private final UUID gameId;

    public BoardCreatedEvent(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }
}
