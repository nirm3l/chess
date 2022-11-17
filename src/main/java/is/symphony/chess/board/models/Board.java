package is.symphony.chess.board.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Board {

    private UUID boardId;

    private Integer minutes;

    private Integer incrementSeconds;

    private List<BoardMove> moves;

    private String state;

    public Board() { }

    public Board(final UUID boardId, final Integer minutes, final Integer incrementSeconds) {
        this.boardId = boardId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
        this.moves = new ArrayList<>();
    }

    public void addMove(BoardMove move) {
        moves.add(move);
    }

    public UUID getBoardId() {
        return boardId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public List<BoardMove> getMoves() {
        return moves;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public void setMoves(final List<BoardMove> moves) {
        this.moves = moves;
    }
}
