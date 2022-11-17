package is.symphony.chess.board.models;

import is.symphony.chess.board.core.models.BoardMove;
import is.symphony.chess.board.core.models.PlayerColor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "boards")
public class Board {

    public final static String INITIAL_STATE = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Id
    private UUID boardId;

    private Integer minutes;

    private Integer incrementSeconds;

    private List<BoardMove> moves;

    private String state = INITIAL_STATE;

    private String san;

    private String result;

    private Long whiteClock;

    private Long blackClock;

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

    public List<BoardMove> getMoves() {
        return moves;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getSan() {
        return san;
    }

    public void setSan(final String san) {
        this.san = san;
    }

    public String getResult() {
        return result;
    }

    public void setResult(final String result) {
        this.result = result;
    }

    public Long getWhiteClock() {
        return whiteClock;
    }

    public Long getLiveWhiteClock() {
        BoardMove lastMove = getLastMove();

        if (result == null && moves.size() > 1 && lastMove != null && PlayerColor.BLACK == lastMove.getPlayerColor()) {
            long clock =  whiteClock - (System.currentTimeMillis() - lastMove.getTimestamp());

            if (clock < 0L) {
                clock = 0L;
            }

            return clock;
        }

        return whiteClock;
    }

    public void setWhiteClock(final Long whiteClock) {
        this.whiteClock = whiteClock;
    }

    public Long getBlackClock() {
        return blackClock;
    }

    public Long getLiveBlackClock() {
        BoardMove lastMove = getLastMove();

        if (result == null && moves.size() > 1 && lastMove != null && PlayerColor.WHITE == lastMove.getPlayerColor()) {
            long clock =  blackClock - (System.currentTimeMillis() - lastMove.getTimestamp());

            if (clock < 0L) {
                clock = 0L;
            }

            return clock;
        }

        return blackClock;
    }

    public void setBlackClock(final Long blackClock) {
        this.blackClock = blackClock;
    }

    private BoardMove getLastMove() {
        return moves.size() > 0 ? moves.get(moves.size() - 1) : null;
    }
}
