package is.symphony.chess.game.controllers;

import is.symphony.chess.game.core.commands.*;
import is.symphony.chess.game.core.exceptions.RequestIsCanceledException;
import is.symphony.chess.game.core.exceptions.RequestNotFoundException;
import is.symphony.chess.game.core.queries.GetRequestQuery;
import is.symphony.chess.game.core.queries.RequestLiveUpdatesQuery;
import is.symphony.chess.game.models.Request;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


@RestController
@RequestMapping("requests")
public class RequestsController {

    private final ReactorCommandGateway reactiveCommandGateway;
    private final ReactorQueryGateway queryGateway;

    public RequestsController(final ReactorCommandGateway reactiveCommandGateway, final ReactorQueryGateway queryGateway) {
        this.reactiveCommandGateway = reactiveCommandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping(value = "/quick-pair")
    public Mono<Request> quickPair(
            @RequestBody final QuickPairRequestCommand quickPairRequestCommand,
            @RequestHeader("User") UUID userId) {
        quickPairRequestCommand.setPlayerId(userId);

        return reactiveCommandGateway.<UUID>send(quickPairRequestCommand).map(Request::new);
    }

    @GetMapping(value = "/{requestId}")
    public Mono<Request> getRequest(@PathVariable final UUID requestId) {
        return queryGateway.query(new GetRequestQuery(requestId), Request.class)
                .switchIfEmpty(Mono.error(new RequestNotFoundException()));
    }

    @GetMapping(value = "/{requestId}/live-updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamRequestLiveUpdates(@PathVariable final UUID requestId) {
        final AtomicReference<Boolean> cancelToken = new AtomicReference<>(false);

        return Flux.merge(
                Flux.interval(Duration.ofMinutes(1)).map(i -> ServerSentEvent.builder().event("ping").build()),
                queryGateway.subscriptionQuery(new RequestLiveUpdatesQuery(requestId), Request.class, Request.class)
                        .switchIfEmpty(Mono.error(new RequestNotFoundException()))
                        .flatMapMany(result -> Flux.concat(result.initialResult().map(this::createServerSentEvent),
                                result.updates().map(this::createServerSentEvent))))
                .flatMap(serverSentEvent -> {
                    if (serverSentEvent.data() instanceof Request) {
                        final Request request = (Request) serverSentEvent.data();

                        if (request.isCanceled()) {
                            cancelToken.set(true);

                            return Flux.just(serverSentEvent, ServerSentEvent.builder().event("ping").build());
                        }
                    }

                    return Mono.just(serverSentEvent);
                }).flatMap(serverSentEvent -> {
                    if (cancelToken.get() && "ping".equals(serverSentEvent.event())) {
                        return Mono.delay(Duration.ofSeconds(1))
                                .flatMap(i -> Mono.error(new RequestIsCanceledException()));
                    }

                    return Mono.just(serverSentEvent);
                });
    }

    private ServerSentEvent<Request> createServerSentEvent(Request request) {
        return ServerSentEvent.<Request>builder().event("request").data(request).build();
    }
}
