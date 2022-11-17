package is.symphony.chess.handlers;

import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "chessAggregateSnapshotTriggerDefinition")
public class ChessBoardAggregate {
    @AggregateIdentifier
    private UUID boardId;

}
