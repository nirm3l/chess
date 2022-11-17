package is.symphony.chess.core.events;

import java.util.UUID;

public class GameInvitationDeclinedEvent {

    private final UUID gameId;

    private final UUID playerId;

    public GameInvitationDeclinedEvent(final UUID gameId, final UUID playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
