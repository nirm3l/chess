package is.symphony.chess.board.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CreateBoardCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    public CreateBoardCommand() {
    }

    public CreateBoardCommand(final UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }
}
