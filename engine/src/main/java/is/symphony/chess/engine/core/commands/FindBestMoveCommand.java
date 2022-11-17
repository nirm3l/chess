package is.symphony.chess.engine.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class FindBestMoveCommand {

    @TargetAggregateIdentifier
    private UUID engineId;

    private UUID gameId;

    private String fen;

    public FindBestMoveCommand() {
    }

    public FindBestMoveCommand(final UUID engineId, final UUID gameId, final String fen) {
        this.engineId = engineId;
        this.gameId = gameId;
        this.fen = fen;
    }

    public UUID getEngineId() {
        return engineId;
    }

    public void setEngineId(final UUID engineId) {
        this.engineId = engineId;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(final String fen) {
        this.fen = fen;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
