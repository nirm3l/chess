package is.symphony.chess.game.handlers;

import is.symphony.chess.board.core.commands.PlayMoveCommand;
import is.symphony.chess.board.core.models.PlayerColor;
import is.symphony.chess.game.core.events.PlayerMovedEvent;
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
}
