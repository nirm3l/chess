package is.symphony.chess.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class PairPlayerWithGameCommand {

    @TargetAggregateIdentifier
    private final UUID gameId;

    private final UUID playerId;

    public PairPlayerWithGameCommand(final UUID gameId, final UUID playerId) {
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
