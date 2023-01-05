package is.symphony.chess.game.handlers;

import is.symphony.chess.board.core.commands.DrawBoardGameCommand;
import is.symphony.chess.board.core.commands.FinishBoardGameCommand;
import is.symphony.chess.board.core.commands.PlayMoveCommand;
import is.symphony.chess.board.core.commands.TakeBackCommand;
import is.symphony.chess.board.core.models.PlayerColor;
import is.symphony.chess.game.core.events.GameDrawEvent;
import is.symphony.chess.game.core.events.GameResignedEvent;
import is.symphony.chess.game.core.events.PlayerMovedEvent;
import is.symphony.chess.game.core.events.TakeBackEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("ChessBoardHandler")
public class ChessBoardHandler {

    private final CommandGateway commandGateway;

    public ChessBoardHandler(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void handle(PlayerMovedEvent playerMovedEvent) {
        commandGateway.send(
                new PlayMoveCommand(playerMovedEvent.getBoardId(),
                        PlayerColor.valueOf(playerMovedEvent.getPlayerColor().toString()),
                        playerMovedEvent.getMove()));
    }

    @EventHandler
    public void handle(GameDrawEvent gameDrawEvent) {
        commandGateway.send(new DrawBoardGameCommand(
                gameDrawEvent.getBoardId(), PlayerColor.valueOf(gameDrawEvent.getPlayerColor().toString())));
    }

    @EventHandler
    public void handle(TakeBackEvent takeBackEvent) {
        commandGateway.send(new TakeBackCommand(
                takeBackEvent.getBoardId(), PlayerColor.valueOf(takeBackEvent.getPlayerColor().toString())));
    }

    @EventHandler
    public void handle(GameResignedEvent gameResignedEvent) {
        commandGateway.send(new FinishBoardGameCommand(
                gameResignedEvent.getBoardId(), PlayerColor.valueOf(gameResignedEvent.getPlayerColor().toString())));
    }
}
