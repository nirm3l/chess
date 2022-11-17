package is.symphony.chess.game.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class FinishGameCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    private String result;

    public FinishGameCommand() { }

    public FinishGameCommand(final UUID gameId, final String result) {
        this.gameId = gameId;
        this.result = result;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(final String result) {
        this.result = result;
    }
}
