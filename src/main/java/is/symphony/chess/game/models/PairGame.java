package is.symphony.chess.game.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "games")
public class Game {
    @Id
    private UUID gameId;

    private Integer minutes;

    private Integer incrementSeconds;

    private UUID whitePlayerId;

    private UUID blackPlayerId;

    private UUID boardId;

    public Game() { }
    public Game(final UUID gameId) {
        this.gameId = gameId;
    }

    public Game(final UUID gameId, final Integer minutes, final Integer incrementSeconds) {
        this.gameId = gameId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
    }

    public UUID getGameId() {
        return gameId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public void setWhitePlayerId(final UUID whitePlayerId) {
        this.whitePlayerId = whitePlayerId;
    }

    public void setBlackPlayerId(final UUID blackPlayerId) {
        this.blackPlayerId = blackPlayerId;
    }

    public UUID getWhitePlayerId() {
        return whitePlayerId;
    }

    public UUID getBlackPlayerId() {
        return blackPlayerId;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }
}