package is.symphony.chess.board.core.events;

import java.util.UUID;

public class BoardCreatedEvent {

    private UUID boardId;

    private Integer incrementSeconds;

    private Integer minutes;

    public BoardCreatedEvent() { }

    public BoardCreatedEvent(final UUID boardId, final Integer increment, final Integer minutes) {
        this.boardId = boardId;
        this.incrementSeconds = increment;
        this.minutes = minutes;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }
}
