package is.symphony.chess.board.controllers;

import is.symphony.chess.board.core.exceptions.BoardNotFoundException;
import is.symphony.chess.board.core.queries.LiveUpdatesQuery;
import is.symphony.chess.board.models.Board;
import is.symphony.chess.board.core.queries.GetBoardQuery;
import is.symphony.chess.board.models.BoardEvent;
import is.symphony.chess.board.services.FenService;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;


@RestController
@RequestMapping("boards")
public class BoardsController {

    private final ReactorQueryGateway queryGateway;

    private final FenService fenService;

    public BoardsController(final ReactorQueryGateway queryGateway, final FenService fenService) {
        this.queryGateway = queryGateway;
        this.fenService = fenService;
    }

    @GetMapping(value = "/{boardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Board> getBoard(@PathVariable final UUID boardId) {
        return queryGateway.query(new GetBoardQuery(boardId), Board.class)
                .switchIfEmpty(Mono.error(new BoardNotFoundException()));
    }

    @GetMapping(value = "/{boardId}/screenshot", produces = MediaType.IMAGE_PNG_VALUE)
    public Flux<DataBuffer> getBoardScreenshot(@PathVariable final UUID boardId) {
        return fenService.getImageContent(boardId)
                .switchIfEmpty(Mono.error(new BoardNotFoundException()));
    }

    @GetMapping(value = "/{boardId}/analysis")
    public Mono<Void> getBoardScreenshot(
            @PathVariable final UUID boardId, ServerHttpResponse response) {

        return fenService.getAnalysisUrl(boardId)
                        .flatMap(url -> {
                            response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
                            response.getHeaders().setLocation(url);
                            return response.setComplete();
                        }).switchIfEmpty(Mono.error(new BoardNotFoundException()));
    }

    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/{boardId}/live-updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> streamBoardMoves(@PathVariable final UUID boardId) {
        return Flux.merge(
                Flux.interval(Duration.ofMinutes(1)).map(i -> ServerSentEvent.builder().event("ping").build()),
                queryGateway.subscriptionQuery(new LiveUpdatesQuery(boardId), Board.class, BoardEvent.class)
                        .switchIfEmpty(Mono.error(new BoardNotFoundException()))
                        .flatMapMany(result -> Flux.concat(result.initialResult().map(board -> createServerSentEvent("board", board)),
                                result.updates().mapNotNull(boardEvent -> {
                                    if (boardEvent.getBoardMove() != null) {
                                        return createServerSentEvent("move", boardEvent.getBoardMove());
                                    }
                                    else if (boardEvent.getError() != null) {
                                        return createServerSentEvent("error", boardEvent.getError());
                                    }
                                    else if (boardEvent.getDrawOffer() != null) {
                                        return createServerSentEvent("draw-offer", boardEvent.getDrawOffer());
                                    }
                                    else if (boardEvent.getResult() != null) {
                                        return createServerSentEvent("result", boardEvent.getResult());
                                    }

                                    return null;
                                }))));
    }

    private <R> ServerSentEvent<R> createServerSentEvent(String event, R boardMove) {
        return ServerSentEvent.<R>builder().event(event).data(boardMove).build();
    }
}
