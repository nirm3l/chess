package is.symphony.chess.game.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class AssociateGameWithBoardCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    private UUID boardId;

    public AssociateGameWithBoardCommand() { }
    public AssociateGameWithBoardCommand(final UUID gameId, final UUID boardId) {
        this.gameId = gameId;
        this.boardId = boardId;
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
}
