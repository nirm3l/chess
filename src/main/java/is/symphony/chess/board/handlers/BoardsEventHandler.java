package is.symphony.chess.game.handlers;

import is.symphony.chess.game.core.events.*;
import is.symphony.chess.game.core.queries.FindGameToPairQuery;
import is.symphony.chess.game.core.models.Game;
import is.symphony.chess.game.core.queries.GetAllGamesQuery;
import is.symphony.chess.game.core.queries.GetGameQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class GamesEventHandler {

    private final Map<UUID, Game> games = new ConcurrentSkipListMap<>();

    private final Map<UUID, Game> gamesToPair = new ConcurrentSkipListMap<>();

    @EventHandler
    public void on(GameCreatedEvent event) {
        if (event.getInvitePlayerId() == null) {
            final Game game = new Game(event.getGameId(), event.getMinutes(), event.getIncrementSeconds());

            gamesToPair.put(event.getGameId(), game);
        }

        final Game game = new Game(event.getGameId(), event.getMinutes(), event.getIncrementSeconds());
        game.setBlackPlayerId(event.getBlackPlayerId());
        game.setWhitePlayerId(event.getWhitePlayerId());

        games.put(game.getGameId(), game);
    }

    @EventHandler
    public void on(GamePairedEvent event) {
        gamesToPair.remove(event.getGameId());

        games.computeIfPresent(event.getGameId(), (key, game) -> {
            if (game.getBlackPlayerId() == null) {
                game.setBlackPlayerId(event.getPlayerId());
            }
            else if (game.getWhitePlayerId() == null) {
                game.setWhitePlayerId(event.getPlayerId());
            }

            return game;
        });
    }

    @EventHandler
    public void on(GameInvitationAcceptedEvent event) {
        games.computeIfPresent(event.getGameId(), (key, game) -> {
            if (game.getBlackPlayerId() == null) {
                game.setBlackPlayerId(event.getPlayerId());
            }
            else if (game.getWhitePlayerId() == null) {
                game.setWhitePlayerId(event.getPlayerId());
            }

            return game;
        });
    }

    @EventHandler
    public void on(BoardAssociatedEvent event) {
        games.computeIfPresent(event.getGameId(), (key, game) -> {
           game.setBoardId(event.getBoardId());

            return game;
        });
    }

    @EventHandler
    public void on(GameInvitationDeclinedEvent event) {
        games.remove(event.getGameId());
    }

    @QueryHandler
    public Publisher<UUID> handle(FindGameToPairQuery query) {
        return Mono.justOrEmpty(gamesToPair.entrySet().stream()
                .filter(uuidGameEntry -> {
                    final Game game = uuidGameEntry.getValue();

                    return !query.getPlayerId().equals(game.getBlackPlayerId()) &&
                            !query.getPlayerId().equals(game.getWhitePlayerId()) &&
                            game.getMinutes().equals(query.getMinutes()) &&
                            game.getIncrementSeconds().equals(query.getIncrementSeconds());
                })
                .map(Map.Entry::getKey).findFirst());
    }

    @QueryHandler
    public Publisher<Game> handle(GetAllGamesQuery query) {
        return Flux.fromIterable(games.values());
    }

    @QueryHandler
    public Publisher<Game> handle(GetGameQuery query) {
        return Mono.justOrEmpty(games.get(query.getGameId()));
    }
}
