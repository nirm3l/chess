package is.symphony.chess.engine.core.events;

import java.util.UUID;

public class BestMoveEvent {

    private UUID engineId;

    private UUID gameId;

    private String move;

    private Double eval;

    public BestMoveEvent() { }

    public BestMoveEvent(final UUID engineId, final UUID gameId, final String move, final Double eval) {
        this.engineId = engineId;
        this.gameId = gameId;
        this.move = move;
        this.eval = eval;
    }

    public UUID getEngineId() {
        return engineId;
    }

    public void setEngineId(final UUID engineId) {
        this.engineId = engineId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public String getMove() {
        return move;
    }

    public void setMove(final String move) {
        this.move = move;
    }

    public Double getEval() {
        return eval;
    }

    public void setEval(final Double eval) {
        this.eval = eval;
    }
}
