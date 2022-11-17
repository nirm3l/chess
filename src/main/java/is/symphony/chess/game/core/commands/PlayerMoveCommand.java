package is.symphony.chess.board.core.commands;

import is.symphony.chess.board.handlers.ChessBoardAggregate;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class PlayMoveCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    private ChessBoardAggregate.PlayerColor playerColor;

    private String move;

    public PlayMoveCommand() {
    }

    public PlayMoveCommand(final UUID gameId, final ChessBoardAggregate.PlayerColor playerColor, final String move) {
        this.gameId = gameId;
        this.playerColor = playerColor;
        this.move = move;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public ChessBoardAggregate.PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(final ChessBoardAggregate.PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getMove() {
        return move;
    }

    public void setMove(final String move) {
        this.move = move;
    }
}
