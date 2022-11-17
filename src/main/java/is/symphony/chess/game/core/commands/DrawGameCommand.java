package is.symphony.chess.game.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class ResignGameCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    private UUID playerId;

    public ResignGameCommand() { }

    public ResignGameCommand(final UUID gameId, final UUID playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }
}
