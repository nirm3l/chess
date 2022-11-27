package is.symphony.chess.player.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class RevertRatingCommand {

    @TargetAggregateIdentifier
    private UUID playerId;

    private UUID gameId;

    private Integer ratingDelta;

    public RevertRatingCommand() {
    }

    public RevertRatingCommand(final UUID playerId, final UUID gameId, final Integer ratingDelta) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.ratingDelta = ratingDelta;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public Integer getRatingDelta() {
        return ratingDelta;
    }

    public void setRatingDelta(final Integer ratingDelta) {
        this.ratingDelta = ratingDelta;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
