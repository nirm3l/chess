package is.symphony.chess.board.models;

import is.symphony.chess.board.core.models.BoardMove;
import is.symphony.chess.board.core.models.PlayerColor;

public class BoardEvent {

    private BoardMove boardMove;

    private String error;

    private PlayerColor drawOffer;

    private PlayerColor takeBackOffer;

    private boolean takeBackAccepted;

    private String result;

    public BoardEvent() {
    }

    public BoardMove getBoardMove() {
        return boardMove;
    }

    public void setBoardMove(final BoardMove boardMove) {
        this.boardMove = boardMove;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public PlayerColor getDrawOffer() {
        return drawOffer;
    }

    public void setDrawOffer(final PlayerColor drawOffer) {
        this.drawOffer = drawOffer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(final String result) {
        this.result = result;
    }

    public PlayerColor getTakeBackOffer() {
        return takeBackOffer;
    }

    public void setTakeBackOffer(final PlayerColor takeBackOffer) {
        this.takeBackOffer = takeBackOffer;
    }

    public boolean isTakeBackAccepted() {
        return takeBackAccepted;
    }

    public void setTakeBackAccepted(final boolean takeBackAccepted) {
        this.takeBackAccepted = takeBackAccepted;
    }
}
