package is.symphony.chess.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class CreateGameCommand {

    @TargetAggregateIdentifier
    private UUID gameId;

    private UUID playerId;

    private UUID invitePlayerId;

    private Integer minutes;

    private Integer incrementSeconds;

    private PieceColor pieceColor;

    public CreateGameCommand() {
    }

    public CreateGameCommand(final UUID gameId, final UUID playerId, final UUID invitePlayerId,
                             final Integer minutes, final Integer incrementSeconds, final PieceColor pieceColor) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.invitePlayerId = invitePlayerId;
        this.minutes = minutes;
        this.incrementSeconds = incrementSeconds;
        this.pieceColor = pieceColor;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getInvitePlayerId() {
        return invitePlayerId;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public Integer getIncrementSeconds() {
        return incrementSeconds;
    }

    public PieceColor getPieceColor() {
        return pieceColor;
    }

    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public void setInvitePlayerId(final UUID invitePlayerId) {
        this.invitePlayerId = invitePlayerId;
    }

    public void setMinutes(final Integer minutes) {
        this.minutes = minutes;
    }

    public void setIncrementSeconds(final Integer incrementSeconds) {
        this.incrementSeconds = incrementSeconds;
    }

    public void setPieceColor(final PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public enum PieceColor {
        WHITE, BLACK, RANDOM;
    }
}
