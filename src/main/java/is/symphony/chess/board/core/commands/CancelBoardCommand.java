package is.symphony.chess.board.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CreateBoardCommand {

    @TargetAggregateIdentifier
    private UUID boardId;

    private Integer minutes;

    private Integer incrementSeconds;

    public CreateBoardCommand() { }

    public CreateBoardCommand(final UUID boardId, final Integer minutes, final Integer incrementSeconds) {
        this.boardId = boardId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }
}
