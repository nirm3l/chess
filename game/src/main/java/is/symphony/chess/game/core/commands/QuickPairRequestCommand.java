package is.symphony.chess.game.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class QuickPairRequestCommand {

    @TargetAggregateIdentifier
    private UUID requestId;

    private UUID playerId;

    private Integer minutes;

    private Integer incrementSeconds;

    public QuickPairRequestCommand() {
        requestId = UUID.randomUUID();
    }

    public QuickPairRequestCommand(final UUID requestId, final UUID playerId, final Integer minutes, final Integer incrementSeconds) {
        super();
        this.requestId = requestId;
        this.playerId = playerId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }
}
