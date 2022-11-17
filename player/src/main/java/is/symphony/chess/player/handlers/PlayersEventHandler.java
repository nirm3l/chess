package is.symphony.chess.player.handlers;

import is.symphony.chess.player.core.events.PlayerEngineAssociatedEvent;
import is.symphony.chess.player.core.events.PlayerRatingUpdatedEvent;
import is.symphony.chess.player.core.events.PlayerRegisteredEvent;
import is.symphony.chess.player.core.queries.GetAllPlayersQuery;
import is.symphony.chess.player.core.queries.GetPlayerQuery;
import is.symphony.chess.player.models.Player;
import is.symphony.chess.player.repositories.PlayerRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

@Service
public class PlayersEventHandler {

    private final PlayerRepository playerRepository;

    public PlayersEventHandler(final PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @EventHandler
    public void on(PlayerRegisteredEvent event) {
        Player player = new Player(event.getPlayerId());
        player.setName(event.getName());
        player.setEmail(event.getEmail());
        player.setBot(event.isBot());
        player.setLevel(event.getLevel());
        player.setRating(event.getRating());

        playerRepository.save(player).block();
    }

    @EventHandler
    public void on(PlayerRatingUpdatedEvent event) {
        playerRepository.findById(event.getPlayerId())
                .flatMap(player -> {
                    player.setRating(event.getRating());

                    return playerRepository.save(player);
                }).block();
    }

    @EventHandler
    public void on(PlayerEngineAssociatedEvent event) {
        playerRepository.findById(event.getPlayerId())
                .flatMap(player -> {
                    player.setEngineId(event.getEngineId());

                    return playerRepository.save(player);
                }).block();
    }

    @QueryHandler
    public Publisher<Player> handle(GetPlayerQuery query) {
        return playerRepository.findById(query.getPlayerId());
    }

    @QueryHandler
    public Publisher<Player> handle(GetAllPlayersQuery query) {
        return playerRepository.findAll();
    }
}
