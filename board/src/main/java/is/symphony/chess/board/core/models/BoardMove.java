package is.symphony.chess.board.core.models;

public class BoardMove {

    private String move;

    private String state;

    private Long timestamp;

    private PlayerColor playerColor;

    private String error;

    private String result;

    private Long clock;

    private PlayerColor drawOffer;

    private PlayerColor takeBackOffer;

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

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(final String result) {
        this.result = result;
    }

    public Long getClock() {
        return clock;
    }

    public void setClock(final Long clock) {
        this.clock = clock;
    }

    public PlayerColor getDrawOffer() {
        return drawOffer;
    }

    public void setDrawOffer(final PlayerColor drawOffer) {
        this.drawOffer = drawOffer;
    }

    public PlayerColor getTakeBackOffer() {
        return takeBackOffer;
    }

    public void setTakeBackOffer(final PlayerColor takeBackOffer) {
        this.takeBackOffer = takeBackOffer;
    }
}
