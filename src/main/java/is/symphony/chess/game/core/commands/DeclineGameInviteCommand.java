package is.symphony.chess.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class DeclineGameInviteCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    private UUID playerId;

    public DeclineGameInviteCommand() { }

    public DeclineGameInviteCommand(final UUID gameId, final UUID playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }
}
