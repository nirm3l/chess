package is.symphony.chess.game.controllers;

import is.symphony.chess.game.core.commands.*;
import is.symphony.chess.game.core.exceptions.GameNotFoundException;
import is.symphony.chess.game.core.queries.LiveUpdatesQuery;
import is.symphony.chess.game.models.Game;
import is.symphony.chess.game.core.queries.GetAllGamesQuery;
import is.symphony.chess.game.core.queries.GetGameQuery;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
    public Mono<Game> createGame(
            @RequestBody final CreateGameCommand createGameCommand,
            @RequestHeader("User") UUID userId) {
        createGameCommand.setGameId(UUID.randomUUID());
        createGameCommand.setPlayerId(userId);

        return reactiveCommandGateway.<UUID>send(createGameCommand).map(Game::new);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Game> getAllGames() {
        return queryGateway.streamingQuery(new GetAllGamesQuery(), Game.class);
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Game> getGame(@PathVariable final UUID gameId) {
        return queryGateway.query(new GetGameQuery(gameId), Game.class)
                .switchIfEmpty(Mono.error(new GameNotFoundException()));
    }

    @PostMapping(value = "/{gameId}/accept-invite")
    public Mono<Void> acceptInvite(
            @PathVariable final UUID gameId, @RequestHeader("User") UUID userId) {
        return reactiveCommandGateway.send(new AcceptGameInviteCommand(gameId, userId)).then();
    }

    @PostMapping(value = "/{gameId}/decline-invite")
    public Mono<Void> declineInvite(
            @PathVariable final UUID gameId, @RequestHeader("User") UUID userId) {
        return reactiveCommandGateway.send(new DeclineGameInviteCommand(gameId, userId)).then();
    }

    @PostMapping(value = "/{gameId}/play")
    public Mono<Void> acceptInvite(@PathVariable final UUID gameId,
                                   @RequestHeader("User") UUID userId,
                                   @RequestBody final PlayerMoveCommand playerMoveCommand) {
        playerMoveCommand.setGameId(gameId);
        playerMoveCommand.setPlayerId(userId);

        return reactiveCommandGateway.send(playerMoveCommand).then();
    }

    @PostMapping(value = "/{gameId}/resign")
    public Mono<Void> resign(
            @PathVariable final UUID gameId, @RequestHeader("User") UUID userId) {
        return reactiveCommandGateway.send(new ResignGameCommand(gameId, userId)).then();
    }

    @PostMapping(value = "/{gameId}/draw")
    public Mono<Void> draw(
            @PathVariable final UUID gameId, @RequestHeader("User") UUID userId) {
        return reactiveCommandGateway.send(new DrawGameCommand(gameId, userId)).then();
    }

    @GetMapping(value = "/{gameId}/live-updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamBoardMoves(@PathVariable final UUID gameId) {
        return Flux.merge(
                Flux.interval(Duration.ofMinutes(1)).map(i -> ServerSentEvent.builder().event("ping").build()),
                queryGateway.subscriptionQuery(new LiveUpdatesQuery(gameId), Game.class, Game.class)
                        .switchIfEmpty(Mono.error(new GameNotFoundException()))
                        .flatMapMany(result -> Flux.concat(result.initialResult().map(this::createServerSentEvent),
                                result.updates().mapNotNull(this::createServerSentEvent))));
    }

    private ServerSentEvent<Game> createServerSentEvent(Game game) {
        return ServerSentEvent.<Game>builder().event("game").data(game).build();
    }
}
