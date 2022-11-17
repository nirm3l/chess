package is.symphony.chess.player.controllers;

import is.symphony.chess.player.core.commands.CheckPlayerExistenceCommand;
import is.symphony.chess.player.core.commands.RegisterPlayerCommand;
import is.symphony.chess.player.core.exceptions.PlayerAlreadyExistException;
import is.symphony.chess.player.core.exceptions.PlayerNotFoundException;
import is.symphony.chess.player.core.queries.GetAllPlayersQuery;
import is.symphony.chess.player.core.queries.GetPlayerQuery;
import is.symphony.chess.player.models.Player;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


@RestController
@RequestMapping("players")
public class PlayersController {

    private final ReactorCommandGateway reactiveCommandGateway;
    private final ReactorQueryGateway queryGateway;

    public PlayersController(final ReactorCommandGateway reactiveCommandGateway, final ReactorQueryGateway queryGateway) {
        this.reactiveCommandGateway = reactiveCommandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping()
    public Mono<Player> registerPlayer(
            @RequestBody final RegisterPlayerCommand registerPlayerCommand) {
        if (registerPlayerCommand.getEmail() != null) {
            registerPlayerCommand.setPlayerId(UUID.nameUUIDFromBytes(registerPlayerCommand.getEmail().getBytes(StandardCharsets.UTF_8)));
        }
        else if (registerPlayerCommand.getName() != null){
            registerPlayerCommand.setPlayerId(UUID.nameUUIDFromBytes(registerPlayerCommand.getName().getBytes(StandardCharsets.UTF_8)));
        }
        else {
            registerPlayerCommand.setPlayerId(UUID.randomUUID());
        }

        return reactiveCommandGateway.<UUID>send(new CheckPlayerExistenceCommand(registerPlayerCommand.getPlayerId()))
                .onErrorResume(e -> reactiveCommandGateway.send(registerPlayerCommand))
                .map(Player::new)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PlayerAlreadyExistException())));

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Player> getAllPlayers() {
        return queryGateway.streamingQuery(new GetAllPlayersQuery(), Player.class);
    }

    @GetMapping(value = "/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Player> getPlayer(@PathVariable final UUID playerId) {
        return queryGateway.query(new GetPlayerQuery(playerId), Player.class)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException()));
    }
}
