package is.symphony.chess.player.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class UpdateRatingCommand {

    @TargetAggregateIdentifier
    private UUID playerId;

    private Integer rating;

    public UpdateRatingCommand() {
    }

    public UpdateRatingCommand(final UUID playerId, final Integer rating) {
        this.playerId = playerId;
        this.rating = rating;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }
}
