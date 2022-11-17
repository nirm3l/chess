package is.symphony.chess.game.handlers;

import is.symphony.chess.game.core.events.*;
import is.symphony.chess.game.core.queries.GetRequestQuery;
import is.symphony.chess.game.core.queries.RequestLiveUpdatesQuery;
import is.symphony.chess.game.models.PairGame;
import is.symphony.chess.game.models.Request;
import is.symphony.chess.game.repositories.PairGameRepository;
import is.symphony.chess.game.repositories.RequestRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GamePairsEventHandler {

    private final PairGameRepository pairGameRepository;

    private final RequestRepository requestRepository;

    private final QueryUpdateEmitter emitter;

    public GamePairsEventHandler(final PairGameRepository pairGameRepository,
                                 final RequestRepository requestRepository, final QueryUpdateEmitter emitter) {
        this.pairGameRepository = pairGameRepository;
        this.requestRepository = requestRepository;
        this.emitter = emitter;
    }

    @EventHandler
    public void on(GameCreatedEvent event) {
        if (event.getInvitePlayerId() == null) {
            final PairGame game = new PairGame(event.getGameId(), event.getMinutes(), event.getIncrementSeconds());

            pairGameRepository.save(game).block();
        }
    }

    @EventHandler
    public void on(GamePairedEvent event) {
        Mono.when(
                pairGameRepository.deleteById(event.getGameId()),
                requestRepository.findById(event.getRequestId())
                        .flatMap(request -> {
                            request.setGameId(event.getGameId());

                            return requestRepository.save(request);
                        })
                        .doOnNext(request -> emitter.emit(RequestLiveUpdatesQuery.class,
                                q -> request.getRequestId().equals(q.getRequestId()), request))).block();
    }

    @EventHandler
    public void on(QuickPairRequestedEvent event) {
        requestRepository.save(new Request(event.getRequestId())).block();
    }

    @EventHandler
    public void on(PlayerPairingCanceledEvent event) {
        requestRepository.findById(event.getRequestId())
                .flatMap(request -> {
                    request.setCanceled(true);

                    return requestRepository.save(request);
                })
                .doOnNext(request -> emitter.emit(RequestLiveUpdatesQuery.class,
                        q -> request.getRequestId().equals(q.getRequestId()), request))
                .block();
    }

    @QueryHandler
    public Publisher<Request> handle(RequestLiveUpdatesQuery query) {
        return requestRepository.findById(query.getRequestId());
    }

    @QueryHandler
    public Publisher<Request> handle(GetRequestQuery query) {
        return requestRepository.findById(query.getRequestId());
    }
}
