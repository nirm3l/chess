package is.symphony.chess.game.core.events;

import java.util.UUID;

public class GameCreatedEvent {

    private UUID gameId;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    private UUID invitePlayerId;

    private Integer minutes;

    private Integer incrementSeconds;

    public GameCreatedEvent() { }
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
