package is.symphony.chess.game.handlers;

import is.symphony.chess.board.core.events.MovePlayedEvent;
import is.symphony.chess.game.core.events.GameDrawEvent;
import is.symphony.chess.game.core.events.GameResignedEvent;
import is.symphony.chess.game.core.events.PlayerMovedEvent;
import is.symphony.chess.game.saga.ChessBoardSaga;
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.SagaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FailedSagasHandler {

    private final SagaRepository<ChessBoardSaga> sagaRepository;

    private final EventStore eventStore;

    private final CommandGateway commandGateway;

    public FailedSagasHandler(final SagaRepository<ChessBoardSaga> sagaRepository,
                              final EventStore eventStore, final CommandGateway commandGateway) {
        this.sagaRepository = sagaRepository;
        this.eventStore = eventStore;
        this.commandGateway = commandGateway;
    }

    @Scheduled(fixedRate = 10000)
    public void start() {
        try {
            sagaRepository.find(new AssociationValue(ChessBoardSaga.RETRY_ASSOCIATION, "true"))
                    .forEach(sagaId -> DefaultUnitOfWork.startAndGet(null)
                            .execute(() -> sagaRepository.load(sagaId).execute(saga -> {
                                saga.setCommandGateway(commandGateway);

                                eventStore.lastSequenceNumberFor(saga.getBoardId().toString())
                                        .ifPresent(seqNum -> {
                                            DomainEventMessage<?> message = eventStore.readEvents(saga.getBoardId().toString(), seqNum).next();

                                            if (message.getPayloadType() == MovePlayedEvent.class) {
                                                saga.handle((MovePlayedEvent)message.getPayload());
                                            }
                                            else if (message.getPayloadType() == GameDrawEvent.class) {
                                                saga.handle((GameDrawEvent)message.getPayload());
                                            }
                                            else if (message.getPayloadType() == GameResignedEvent.class) {
                                                saga.handle((GameResignedEvent)message.getPayload());
                                            }
                                            else if (message.getPayloadType() == PlayerMovedEvent.class) {
                                                saga.handle((PlayerMovedEvent)message.getPayload());
                                            }

                                            saga.retrySuccess();
                                        });
                            })));
        }
        catch (NoHandlerForCommandException e) {
            //ignore
        }
    }
}
