package is.symphony.chess.game.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "pair_games")
public class PairGame {
    @Id
    private UUID gameId;

    private Integer minutes;

    private Integer incrementSeconds;


    public PairGame() { }
    public PairGame(final UUID gameId) {
        this.gameId = gameId;
    }

    public PairGame(final UUID gameId, final Integer minutes, final Integer incrementSeconds) {
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
}