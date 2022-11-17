package is.symphony.chess.engine.handlers;

import is.symphony.chess.engine.configuration.ChessProvider;
import is.symphony.chess.engine.core.commands.FindBestMoveCommand;
import is.symphony.chess.engine.core.commands.RegisterEngineCommand;
import is.symphony.chess.engine.core.events.BestMoveEvent;
import is.symphony.chess.engine.core.events.EngineRegisteredEvent;
import is.symphony.chess.engine.core.exceptions.CannotFindMoveException;
import is.symphony.chess.engine.utils.ChessUCI;
import net.andreinc.neatchess.client.UCIResponse;
import net.andreinc.neatchess.client.model.Analysis;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "chessAggregateSnapshotTriggerDefinition")
public class EngineAggregate {

    @AggregateIdentifier
    private UUID engineId;

    private String name;

    private Integer level;

    private transient ChessProvider chessProvider;

    @Autowired
    public void setChessProvider(final ChessProvider chessProvider) {
        this.chessProvider = chessProvider;
    }

    protected EngineAggregate() { }

    @CommandHandler
    public EngineAggregate(RegisterEngineCommand command) {
        AggregateLifecycle.apply(new EngineRegisteredEvent(command.getEngineId(), command.getName(), command.getLevel()));
    }

    @CommandHandler
    public void handle(FindBestMoveCommand command) {
        Analysis analysis = getAnalysis(command.getFen());

        if (analysis != null && analysis.getBestMove() != null) {
            AggregateLifecycle.apply(new BestMoveEvent(
                    engineId, command.getGameId(), analysis.getBestMove().getLan(), analysis.getBestMove().getStrength().getScore()));
        }
        else if (analysis != null && !analysis.isMate() && !analysis.isDraw()) {
            throw new CannotFindMoveException();
        }
    }

    private Analysis getAnalysis(final String fen) {
        final ChessUCI chessUCI = chessProvider.getChessUCI();

        try {
            chessUCI.setOption("Skill Level", level.toString());
            chessUCI.uciNewGame();

            if (fen != null) {
                chessUCI.positionFen(fen);
            }

            UCIResponse<Analysis> response = chessUCI.analysis(level);
            return response.getResultOrThrow();
        } finally {
            chessUCI.close();
        }
    }

    @EventSourcingHandler
    public void on(EngineRegisteredEvent event) {
        engineId = event.getEngineId();
        name = event.getName();
        level = event.getLevel();
    }
}