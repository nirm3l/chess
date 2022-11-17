package is.symphony.chess.board.core.commands;

import is.symphony.chess.board.core.models.PlayerColor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class PlayMoveCommand {

    @TargetAggregateIdentifier
    private UUID boardId;

    private PlayerColor playerColor;

    private String move;

    public PlayMoveCommand() { }

    public PlayMoveCommand(final UUID boardId, final PlayerColor playerColor, final String move) {
        this.boardId = boardId;
        this.playerColor = playerColor;
        this.move = move;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(final PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getMove() {
        return move;
    }

    public void setMove(final String move) {
        this.move = move;
    }
}
