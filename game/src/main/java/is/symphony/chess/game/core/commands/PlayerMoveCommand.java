package is.symphony.chess.game.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class PlayerMoveCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    private UUID playerId;

    private String move;

    public PlayerMoveCommand() { }

    public PlayerMoveCommand(final UUID gameId, final UUID playerId, final String move) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.move = move;
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

    public String getMove() {
        return move;
    }

    public void setMove(final String move) {
        this.move = move;
    }
}
