package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GameCreatedEvent {

    private final UUID gameId;

    private final UUID whitePlayerId;

    private final UUID blackPlayerId;

    private final UUID invitePlayerId;

    private final Integer minutes;

    private final Integer incrementSeconds;

    public GameCreatedEvent(final UUID gameId, final UUID whitePlayerId, final UUID blackPlayerId,
                            final UUID invitePlayerId, final Integer minutes, final Integer incrementSeconds) {
        this.gameId = gameId;
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.invitePlayerId = invitePlayerId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getGameId() {
        return gameId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public UUID getWhitePlayerId() {
        return whitePlayerId;
    }

    public UUID getBlackPlayerId() {
        return blackPlayerId;
    }

    public UUID getInvitePlayerId() {
        return invitePlayerId;
    }
}
