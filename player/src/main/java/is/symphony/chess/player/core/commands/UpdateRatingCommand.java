package is.symphony.chess.player.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateVersion;

import java.util.UUID;

public class UpdateRatingCommand {

    @TargetAggregateIdentifier
    private UUID playerId;

    private Integer ratingDelta;

    @TargetAggregateVersion
    private Long version;

    public UpdateRatingCommand() {
    }

    public UpdateRatingCommand(final UUID playerId, final Integer ratingDelta, final Long version) {
        this.playerId = playerId;
        this.ratingDelta = ratingDelta;
        this.version = version;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }
}
