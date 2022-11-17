package is.symphony.chess.board.core.commands;

import is.symphony.chess.board.core.models.PlayerColor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class FinishBoardGameCommand {

    @TargetAggregateIdentifier
    private UUID boardId;

    private PlayerColor winner;

    private boolean timeIsUp;

    public FinishBoardGameCommand() { }

    public FinishBoardGameCommand(final UUID boardId, final PlayerColor winner) {
        this.boardId = boardId;
        this.winner = winner;
    }

    public UUID getBoardId() {
        return boardId;
    }

    public void setBoardId(final UUID boardId) {
        this.boardId = boardId;
    }

    public PlayerColor getWinner() {
        return winner;
    }

    public void setWinner(final PlayerColor winner) {
        this.winner = winner;
    }

    public boolean isTimeIsUp() {
        return timeIsUp;
    }

    public void setTimeIsUp(final boolean timeIsUp) {
        this.timeIsUp = timeIsUp;
    }
}
