package is.symphony.chess.board.services;

import is.symphony.chess.board.core.models.BoardMove;
import is.symphony.chess.board.core.models.PlayerColor;
import is.symphony.chess.board.models.Board;
import is.symphony.chess.board.core.queries.GetBoardQuery;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Service
public class FenService {

    private final static String FEN_SERVICE_URL = "https://fen2image.chessvision.ai/";

    private final static String ANALYSIS_SERVICE_URL = "https://lichess.org/analysis/";

    private final WebClient webClient;

    private final ReactorQueryGateway reactorQueryGateway;

    public FenService(final ReactorQueryGateway reactorQueryGateway) {
        this.reactorQueryGateway = reactorQueryGateway;
        this.webClient = WebClient.builder().baseUrl(FEN_SERVICE_URL).build();
    }

    public Flux<DataBuffer> getImageContent(final UUID boardId) {
        return reactorQueryGateway.query(new GetBoardQuery(boardId), Board.class)
                .flatMapMany(board -> {
                    BoardMove lastMove = board.getMoves().size() > 0 ?
                            board.getMoves().get(board.getMoves().size() - 1) : null;

                    String state = board.getState() != null ? board.getState() : Board.INITIAL_STATE;

                    return webClient.get().uri(uriBuilder -> uriBuilder
                                    .path(state)
                                    .queryParam("turn", lastMove != null && lastMove.getPlayerColor() == PlayerColor.WHITE ? "black" : "white")
                                    .build())
                            .retrieve().bodyToFlux(DataBuffer.class);
                });
    }

    public Mono<URI> getAnalysisUrl(final UUID boardId) {
        return reactorQueryGateway.query(new GetBoardQuery(boardId), Board.class)
                .map(board -> {
                    String state = board.getState() != null ? board.getState() : Board.INITIAL_STATE;

                    return URI.create(ANALYSIS_SERVICE_URL.concat(state.replace(" ", "%20")));
                });
    }
}
