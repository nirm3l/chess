package is.symphony.chess.board.core.models;

public class BoardMove {

    private String move;

    private String state;

    private Long timestamp;

    private PlayerColor playerColor;

    public BoardMove() { }

    public BoardMove(final String move, final String state, final Long timestamp, final PlayerColor playerColor) {
        this.move = move;
        this.state = state;
        this.timestamp = timestamp;
        this.playerColor = playerColor;
    }

    public String getMove() {
        return move;
    }

    public void setMove(final String move) {
        this.move = move;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(final PlayerColor playerColor) {
        this.playerColor = playerColor;
    }
}
