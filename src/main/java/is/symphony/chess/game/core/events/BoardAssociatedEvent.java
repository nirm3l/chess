package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GameInvitationAcceptedEvent {

    private final UUID gameId;

    private final UUID playerId;

    public GameInvitationAcceptedEvent(final UUID gameId, final UUID playerId) {
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
