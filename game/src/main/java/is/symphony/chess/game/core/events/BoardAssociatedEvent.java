package is.symphony.chess.game.core.events;

import java.util.UUID;

public class BoardAssociatedEvent {

    private UUID gameId;

    private UUID boardId;

    private UUID playerWhite;

    private UUID playerBlack;

    public BoardAssociatedEvent() { }

    public BoardAssociatedEvent(final UUID gameId, final UUID boardId, final UUID playerWhite, final UUID playerBlack) {
        this.gameId = gameId;
        this.boardId = boardId;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public UUID getPlayerWhite() {
        return playerWhite;
    }

    public void setPlayerWhite(final UUID playerWhite) {
        this.playerWhite = playerWhite;
    }

    public UUID getPlayerBlack() {
        return playerBlack;
    }

    public void setPlayerBlack(final UUID playerBlack) {
        this.playerBlack = playerBlack;
    }
}
