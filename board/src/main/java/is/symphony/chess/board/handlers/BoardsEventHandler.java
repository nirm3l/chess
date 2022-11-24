package is.symphony.chess.board.handlers;

import is.symphony.chess.board.core.events.*;
import is.symphony.chess.board.core.models.BoardMove;
import is.symphony.chess.board.core.models.PlayerColor;
import is.symphony.chess.board.core.queries.LiveUpdatesQuery;
import is.symphony.chess.board.models.Board;
import is.symphony.chess.board.core.queries.GetBoardQuery;
import is.symphony.chess.board.models.BoardEvent;
import is.symphony.chess.board.repositories.BoardRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("BoardsEventHandler")
public class BoardsEventHandler {

    private final BoardRepository boardRepository;

    private final QueryUpdateEmitter emitter;

    public BoardsEventHandler(final BoardRepository boardRepository, final QueryUpdateEmitter emitter) {
        this.boardRepository = boardRepository;
        this.emitter = emitter;
    }

    @EventHandler
    public void on(BoardCreatedEvent event) {
        Board board = new Board(event.getBoardId(), event.getMinutes(), event.getIncrementSeconds());
        board.setBlackClock(event.getMinutes() * ChessBoardAggregate.MINUTE_MILLIS);
        board.setWhiteClock(board.getBlackClock());

        boardRepository.save(board).block();
    }

    @EventHandler
    public void on(BoardCanceledEvent event) {
        boardRepository.deleteById(event.getBoardId()).block();
    }

    @EventHandler
    public void on(MovePlayedEvent event) {
        final BoardEvent boardEvent = new BoardEvent();
        boardEvent.setBoardMove(event.getBoardMove());

        emitter.emit(LiveUpdatesQuery.class, q -> event.getBoardId().equals(q.getBoardId()), boardEvent);

        boardRepository.findById(event.getBoardId())
                        .flatMap(board -> {
                            board.addMove(event.getBoardMove());
                            board.setState(event.getBoardState());
                            board.setSan(event.getBoardSan());

                            if (event.getBoardMove().getPlayerColor() == PlayerColor.WHITE) {
                                board.setWhiteClock(event.getBoardMove().getClock());
                            }
                            else {
                                board.setBlackClock(event.getBoardMove().getClock());
                            }

                            return boardRepository.save(board);
                        }).block();
    }

    @EventHandler
    public void on(DrawOfferedEvent event) {
        boardRepository.findById(event.getBoardId())
                .flatMap(board -> {
                    BoardMove lastMove = getLastMove(board);

                    if (lastMove != null) {
                        lastMove.setDrawOffer(event.getPlayerColor());
                    }

                    BoardEvent boardEvent = new BoardEvent();
                    boardEvent.setDrawOffer(event.getPlayerColor());

                    emitter.emit(LiveUpdatesQuery.class, q -> event.getBoardId()
                            .equals(q.getBoardId()), boardEvent);

                    return boardRepository.save(board);
                }).block();
    }

    private BoardMove getLastMove(Board board) {
        return board.getMoves().size() > 0 ?
                board.getMoves().get(board.getMoves().size() - 1) : null;
    }

    @EventHandler
    public void on(BoardGameFinishedEvent event) {
        boardRepository.findById(event.getBoardId())
                .flatMap(board -> {
                    board.setResult(event.getResult());

                    BoardMove lastMove = getLastMove(board);

                    if (event.isTimeIsUp() && lastMove != null) {
                        if (lastMove.getPlayerColor() == PlayerColor.WHITE) {
                            board.setBlackClock(0L);
                        }
                        else {
                            board.setWhiteClock(0L);
                        }
                    }

                    BoardEvent boardEvent = new BoardEvent();
                    boardEvent.setResult(event.getResult());

                    emitter.emit(LiveUpdatesQuery.class, q -> event.getBoardId()
                            .equals(q.getBoardId()), boardEvent);

                    return boardRepository.save(board);
                }).block();
    }

    @EventHandler
    public void handle(BoardErrorEvent boardErrorEvent) {
        final BoardEvent boardEvent = new BoardEvent();
        boardEvent.setError(boardErrorEvent.getMessage());

        emitter.emit(LiveUpdatesQuery.class, q -> boardErrorEvent.getBoardId().equals(q.getBoardId()), boardEvent);
    }

    @QueryHandler
    public Publisher<Board> handle(GetBoardQuery query) {
        return boardRepository.findById(query.getBoardId());
    }

    @QueryHandler
    public Publisher<Board> handle(LiveUpdatesQuery query) {
        return boardRepository.findById(query.getBoardId());
    }
}
