package is.symphony.chess.board.core.commands;

import is.symphony.chess.board.core.models.PlayerColor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class TakeBackCommand {

    @TargetAggregateIdentifier
    private UUID boardId;

    private PlayerColor playerColor;

    public TakeBackCommand() { }

    public TakeBackCommand(final UUID boardId, final PlayerColor playerColor) {
        this.boardId = boardId;
        this.playerColor = playerColor;
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
}
