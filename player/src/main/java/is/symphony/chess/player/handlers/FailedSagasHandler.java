package is.symphony.chess.player.handlers;

import is.symphony.chess.player.core.events.PlayerRegisteredEvent;
import is.symphony.chess.player.saga.PlayerEngineSaga;
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

    private final SagaRepository<PlayerEngineSaga> sagaRepository;

    private final EventStore eventStore;

    private final CommandGateway commandGateway;

    public FailedSagasHandler(final SagaRepository<PlayerEngineSaga> sagaRepository,
                              final EventStore eventStore, final CommandGateway commandGateway) {
        this.sagaRepository = sagaRepository;
        this.eventStore = eventStore;
        this.commandGateway = commandGateway;
    }

    @Scheduled(fixedRate = 10000)
    public void start() {
        try {
            sagaRepository.find(new AssociationValue(PlayerEngineSaga.RETRY_ASSOCIATION, "true"))
                    .forEach(sagaId -> DefaultUnitOfWork.startAndGet(null)
                            .execute(() -> sagaRepository.load(sagaId)
                                    .execute(saga -> {
                                        saga.setCommandGateway(commandGateway);

                                        eventStore.lastSequenceNumberFor(saga.getPlayerId().toString())
                                                .ifPresent(seqNum -> {
                                                    DomainEventMessage<?> message = eventStore.readEvents(saga.getPlayerId().toString(), seqNum).next();

                                                    if (message.getPayloadType() == PlayerRegisteredEvent.class) {
                                                        saga.handle((PlayerRegisteredEvent)message.getPayload());
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
