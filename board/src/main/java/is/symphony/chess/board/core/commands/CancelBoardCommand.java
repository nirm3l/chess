package is.symphony.chess.board.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CancelBoardCommand {

    @TargetAggregateIdentifier
    private UUID boardId;

    public CancelBoardCommand() { }

    public CancelBoardCommand(final UUID boardId) {
        this.boardId = boardId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}
