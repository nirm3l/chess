package is.symphony.chess.board.handlers;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import is.symphony.chess.board.core.commands.*;
import is.symphony.chess.board.core.events.*;
import is.symphony.chess.board.core.exceptions.*;
import is.symphony.chess.board.core.models.BoardGameResult;
import is.symphony.chess.board.core.models.BoardMove;
import is.symphony.chess.board.core.models.PlayerColor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "gameAggregateSnapshotTriggerDefinition")
public class ChessBoardAggregate {

    public final static Long SECOND_MILLIS = 1000L;
    public final static Long MINUTE_MILLIS = 60 * SECOND_MILLIS;

    @AggregateIdentifier
    private UUID boardId;

    private List<BoardMove> moves;

    private Integer incrementSeconds = 0;

    private Integer minutes;

    private String state;

    private String san;

    private String result;

    private boolean canceled;

    private transient Board board;

    private PlayerColor drawOffer;

    protected ChessBoardAggregate() { }

    @CommandHandler
    public ChessBoardAggregate(CreateBoardCommand command) {
        AggregateLifecycle.apply(new BoardCreatedEvent(command.getBoardId(), command.getIncrementSeconds(), command.getMinutes()));
    }

    @CommandHandler
    public void handle(PlayMoveCommand command) {
        if (result != null) {
            throw new IllegalMoveException();
        }

        if (whoPlays() != command.getPlayerColor()) {
            throw new ItsNotYourTurnException(boardId);
        }

        Tuple2<String, String> tuple2 = getFenAndSanAfterMove(command.getMove(), command.getPlayerColor());

        if (tuple2 != null) {
            final BoardMove boardMove = new BoardMove(
                    command.getMove(), getBoard().getFen(), System.currentTimeMillis(), whoPlays());

            List<BoardMove> newMoves = new ArrayList<>(moves);
            newMoves.add(boardMove);

            Tuple2<Long, Long> clocks = getClocks(newMoves, incrementSeconds);

            if (boardMove.getPlayerColor() == PlayerColor.WHITE) {
                boardMove.setClock(minutes * MINUTE_MILLIS - clocks.getT1());
            }
            else {
                boardMove.setClock(minutes * MINUTE_MILLIS - clocks.getT2());
            }

            AggregateLifecycle.apply(new MovePlayedEvent(
                    command.getBoardId(), boardMove, tuple2.getT1(), tuple2.getT2()));
        }
        else {
            throw new IllegalMoveException(boardId);
        }
    }

    @CommandHandler
    public void handle(CancelBoardCommand command) {
        if (canceled) {
            throw new BoardIsCanceledException();
        }

        AggregateLifecycle.apply(new BoardCanceledEvent(command.getBoardId()));
    }

    @CommandHandler
    public void handle(DrawBoardGameCommand command) {
        if (drawOffer == null) {
            AggregateLifecycle.apply(
                    new DrawOfferedEvent(command.getBoardId(), command.getPlayerColor()));
        }
        else if (drawOffer != command.getPlayerColor()) {
            AggregateLifecycle.apply(new BoardGameFinishedEvent(boardId, "1/2-1/2", false));
        }
    }

    @CommandHandler
    public void handle(FinishBoardGameCommand finishBoardGameCommand) {
        if (result != null) {
            throw new BoardGameIsFinishedException();
        }

        String result = "1/2-1/2";

        if (PlayerColor.WHITE.equals(finishBoardGameCommand.getWinner())) {
            result = "1-0";
        }
        else if (PlayerColor.BLACK.equals(finishBoardGameCommand.getWinner())) {
            result = "0-1";
        }

        AggregateLifecycle.apply(new BoardGameFinishedEvent(boardId, result, finishBoardGameCommand.isTimeIsUp()));
    }

    @ExceptionHandler
    public void handleBoardExceptions(BoardException exception) {
        AggregateLifecycle.apply(new BoardErrorEvent(exception.getBoardId(), exception.getMessage()));
    }

    private void addSanMove(MoveList moveList, String move, PlayerColor playerColor) {
        try {
            Move moveInstance = new Move(move, playerColor == PlayerColor.WHITE ? Side.WHITE : Side.BLACK);
            // TODO: Promotion?

            moveList.add(moveInstance);
        }
        catch (Exception e) {
            moveList.addSanMove(playerColor == PlayerColor.WHITE ? move.toUpperCase() : move, true, true);
        }
    }

    private PlayerColor whoPlays() {
        return moves.size() % 2 == 0 ? PlayerColor.WHITE : PlayerColor.BLACK;
    }

    private Tuple2<String, String> getFenAndSanAfterMove(String move, PlayerColor playerColor) {
        try {
            List<Move> legalMoves = getBoard().legalMoves();

            final MoveList moveList = new MoveList();

            if (san != null) {
                moveList.loadFromSan(san);
            }

            addSanMove(moveList, move, playerColor);

            if(legalMoves.contains(moveList.getLast())) {
                return Tuples.of(moveList.getFen(), moveList.toSanWithMoveNumbers());
            }
        }
        catch (MoveConversionException e) {
            return null;
        }

        return null;
    }

    private Board getBoard() {
        if (board == null) {
            board = new Board();

            if (state != null) {
                board.loadFromFen(state);
            }
        }

        return board;
    }

    @EventSourcingHandler
    public void on(BoardCreatedEvent event) {
        boardId = event.getBoardId();
        moves = new ArrayList<>();
        incrementSeconds = event.getIncrementSeconds();
        minutes = event.getMinutes();
    }

    @EventSourcingHandler
    public void on(MovePlayedEvent event) {
        final String move = event.getBoardMove().getMove();
        getBoard().doMove(move);

        moves.add(event.getBoardMove());

        state = event.getBoardState();
        san = event.getBoardSan();
        drawOffer = null;

        BoardGameResult result = getBoardGameResult();

        if (result != null) {
            AggregateLifecycle.apply(new BoardGameFinishedEvent(boardId, result.toString(), false));
        }
    }

    @EventSourcingHandler
    public void on(BoardGameFinishedEvent event) {
        this.result = event.getResult();
    }

    @EventSourcingHandler
    public void on(BoardCanceledEvent event) {
        canceled = true;

        AggregateLifecycle.markDeleted();
    }

    @EventSourcingHandler
    public void on(DrawOfferedEvent event) {
        drawOffer = event.getPlayerColor();
    }

    private BoardGameResult getBoardGameResult() {
        if (getBoard().isMated()) {
            return getBoard().getSideToMove() == Side.BLACK ? BoardGameResult.WIN_1_0 : BoardGameResult.WIN_0_1;
        }
        else if (getBoard().isDraw()) {
            return BoardGameResult.DRAW;
        }

        return null;
    }

    private static Tuple2<Long, Long> getClocks(List<BoardMove> moves, Integer incrementSeconds) {
        long whiteClock = 0L;
        long blackClock = 0L;

        Long currentTimestamp = System.currentTimeMillis();

        for (int i = moves.size() - 1; i > 0; i--) {
            final BoardMove boardMove = moves.get(i);

            if (incrementSeconds > 0 && i > 1) {
                if (boardMove.getPlayerColor() == PlayerColor.WHITE) {
                    whiteClock -= incrementSeconds * SECOND_MILLIS;
                }
                else {
                    blackClock -= incrementSeconds * SECOND_MILLIS;
                }
            }

            if (boardMove.getPlayerColor() == PlayerColor.WHITE) {
                blackClock += currentTimestamp - boardMove.getTimestamp();
            }
            else {
                whiteClock += currentTimestamp - boardMove.getTimestamp();
            }

            currentTimestamp = boardMove.getTimestamp();
        }

        return Tuples.of(whiteClock, blackClock);
    }
}
