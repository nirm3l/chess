package is.symphony.chess.game.controllers;

import is.symphony.chess.game.core.commands.*;
import is.symphony.chess.game.core.models.Game;
import is.symphony.chess.game.core.queries.GetAllGamesQuery;
import is.symphony.chess.game.core.queries.GetGameQuery;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RestController
@RequestMapping("games")
public class GamesController {

    private final ReactorCommandGateway reactiveCommandGateway;
    private final ReactorQueryGateway queryGateway;

    public GamesController(final ReactorCommandGateway reactiveCommandGateway, final ReactorQueryGateway queryGateway) {
        this.reactiveCommandGateway = reactiveCommandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping()
    public Mono<Game> createGame(@RequestBody final CreateGameCommand createGameCommand) {
        createGameCommand.setGameId(UUID.randomUUID());

        return reactiveCommandGateway.<UUID>send(createGameCommand).map(Game::new);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Game> getAllGames() {
        return queryGateway.streamingQuery(new GetAllGamesQuery(), Game.class);
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Game> getGame(@PathVariable final UUID gameId) {
        return queryGateway.query(new GetGameQuery(gameId), Game.class);
    }

    @PostMapping(value = "/quick-pair")
    public Mono<Void> quickPair(@RequestBody final QuickPairRequestCommand quickPairRequestCommand) {
        return reactiveCommandGateway.send(quickPairRequestCommand).then();
    }

    @PostMapping(value = "/{gameId}/accept-invite")
    public Mono<Void> acceptInvite(@PathVariable final UUID gameId,
            @RequestBody final AcceptGameInviteCommand acceptGameInviteCommand) {
        acceptGameInviteCommand.setGameId(gameId);

        return reactiveCommandGateway.send(acceptGameInviteCommand).then();
    }

    @PostMapping(value = "/{gameId}/decline-invite")
    public Mono<Void> declineInvite(@PathVariable final UUID gameId,
                                   @RequestBody final DeclineGameInviteCommand declineGameInviteCommand) {
        declineGameInviteCommand.setGameId(gameId);

        return reactiveCommandGateway.send(declineGameInviteCommand).then();
    }

    @PostMapping(value = "/{gameId}/play")
    public Mono<Void> acceptInvite(@PathVariable final UUID gameId,
                                   @RequestBody final PlayerMoveCommand playerMoveCommand) {
        playerMoveCommand.setGameId(gameId);

        return reactiveCommandGateway.send(playerMoveCommand).then();
    }
}
