package is.symphony.chess.board.core.events;

import is.symphony.chess.board.core.models.BoardMove;

import java.util.UUID;

public class MovePlayedEvent {

    private UUID boardId;

    private BoardMove boardMove;

    private String boardState;

    private String boardSan;

    public MovePlayedEvent() { }

    public MovePlayedEvent(final UUID boardId, final BoardMove boardMove, final String boardState, final String boardSan) {
        this.boardId = boardId;
        this.boardMove = boardMove;
        this.boardState = boardState;
        this.boardSan = boardSan;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public BoardMove getBoardMove() {
        return boardMove;
    }

    public void setBoardMove(final BoardMove boardMove) {
        this.boardMove = boardMove;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(final String boardState) {
        this.boardState = boardState;
    }

    public String getBoardSan() {
        return boardSan;
    }

    public void setBoardSan(final String boardSan) {
        this.boardSan = boardSan;
    }
}
