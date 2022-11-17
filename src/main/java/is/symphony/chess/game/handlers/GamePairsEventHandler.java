package is.symphony.chess.game.handlers;

import is.symphony.chess.game.core.events.*;
import is.symphony.chess.game.core.queries.FindGameToPairQuery;
import is.symphony.chess.game.core.queries.LiveUpdatesQuery;
import is.symphony.chess.game.models.Game;
import is.symphony.chess.game.core.queries.GetAllGamesQuery;
import is.symphony.chess.game.core.queries.GetGameQuery;
import is.symphony.chess.game.models.PairGame;
import is.symphony.chess.game.repositories.GameRepository;
import is.symphony.chess.game.repositories.PairGameRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GamesEventHandler {

    private final GameRepository gameRepository;

    private final PairGameRepository pairGameRepository;

    //TODO: Split GamesEventHandler and PairGamesEventHandler and implement request query inside PairGamesEventHandler

    private final QueryUpdateEmitter emitter;

    public GamesEventHandler(
            final GameRepository gameRepository, final PairGameRepository pairGameRepository,
            final QueryUpdateEmitter emitter) {
        this.gameRepository = gameRepository;
        this.pairGameRepository = pairGameRepository;
        this.emitter = emitter;
    }

    @EventHandler
    public void on(GameCreatedEvent event) {
        Mono<PairGame> pairGameMono = Mono.empty();

        if (event.getInvitePlayerId() == null) {
            final PairGame game = new PairGame(event.getGameId(), event.getMinutes(), event.getIncrementSeconds());

            pairGameMono = pairGameRepository.save(game);
        }

        final Game game = new Game(event.getGameId(), event.getMinutes(), event.getIncrementSeconds());
        game.setBlackPlayerId(event.getBlackPlayerId());
        game.setWhitePlayerId(event.getWhitePlayerId());

        Mono.when(pairGameMono, gameRepository.save(game)).block();
    }

    @EventHandler
    public void on(GamePairedEvent event) {
        //TODO: Handle requestId

        Mono.when(pairGameRepository.deleteById(event.getGameId()),
                updateGame(event.getGameId(), event.getPlayerId())
                        .doOnNext(game -> emitter.emit(LiveUpdatesQuery.class, q -> event.getGameId()
                                .equals(q.getGameId()), game))).block();
    }

    @EventHandler
    public void on(GameInvitationAcceptedEvent event) {
        updateGame(event.getGameId(), event.getPlayerId())
                .doOnNext(game -> emitter.emit(LiveUpdatesQuery.class, q -> event.getGameId()
                        .equals(q.getGameId()), game)).block();
    }

    private Mono<Game> updateGame(UUID gameId, UUID playerId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getBlackPlayerId() == null) {
                        game.setBlackPlayerId(playerId);
                    }
                    else if (game.getWhitePlayerId() == null) {
                        game.setWhitePlayerId(playerId);
                    }

                    return gameRepository.save(game);
                });
    }

    @EventHandler
    public void on(BoardAssociatedEvent event) {
        gameRepository.findById(event.getGameId())
                .flatMap(game -> {
                    game.setBoardId(event.getBoardId());

                    return gameRepository.save(game);
                }).doOnNext(game -> emitter.emit(LiveUpdatesQuery.class, q -> event.getGameId()
                        .equals(q.getGameId()), game)).block();
    }

    @EventHandler
    public void on(GameFinishedEvent event) {
        gameRepository.findById(event.getGameId())
                .flatMap(game -> {
                    game.setResult(event.getResult());

                    return gameRepository.save(game);
                }).doOnNext(game -> emitter.emit(LiveUpdatesQuery.class, q -> event.getGameId()
                        .equals(q.getGameId()), game)).block();
    }

    @EventHandler
    public void on(GameInvitationDeclinedEvent event) {
        gameRepository.deleteById(event.getGameId())
                .block();

        cancelGameEvent(event.getGameId());
    }

    @EventHandler
    public void on(GameCanceledEvent event) {
        gameRepository.deleteById(event.getGameId()).block();

        cancelGameEvent(event.getGameId());
    }

    private void cancelGameEvent(UUID gameId) {
        final Game game = new Game();
        game.setGameId(gameId);
        game.setCanceled(true);

        emitter.emit(LiveUpdatesQuery.class, q -> gameId.equals(q.getGameId()), game);
    }

    @QueryHandler
    public Publisher<UUID> handle(FindGameToPairQuery query) {
        return gameRepository.findAll().filter(game -> !query.getPlayerId().equals(game.getBlackPlayerId()) &&
                !query.getPlayerId().equals(game.getWhitePlayerId()) &&
                game.getMinutes().equals(query.getMinutes()) &&
                game.getIncrementSeconds().equals(query.getIncrementSeconds()))
                .map(Game::getGameId).next();
    }

    @QueryHandler
    public Publisher<Game> handle(GetAllGamesQuery query) {
        return gameRepository.findAll();
    }

    @QueryHandler
    public Publisher<Game> handle(GetGameQuery query) {
        return gameRepository.findById(query.getGameId());
    }

    @QueryHandler
    public Publisher<Game> handle(LiveUpdatesQuery query) {
        return gameRepository.findById(query.getGameId());
    }
}
