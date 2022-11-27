package is.symphony.chess;

import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.Saga;
import org.axonframework.modelling.saga.SagaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class FailedSagasHandler<T> {

    private static final Logger LOG = LoggerFactory.getLogger(FailedSagasHandler.class);

    private final SagaRepository<T> sagaRepository;

    private final EventStore eventStore;

    private final static String RETRY_KEY = "retry";

    public final static AssociationValue RETRY_ASSOCIATION = new AssociationValue(RETRY_KEY, "value");

    private final static String RETRY_SEQ_NUM_KEY = "retrySeqNum";

    private final static String RETRY_AGGREGATE_ID_KEY = "retryAggregateId";

    private final String[] handleMethods;

    public FailedSagasHandler(final SagaRepository<T> sagaRepository,
                              final EventStore eventStore) {
        this(sagaRepository, eventStore, new String[]{});
    }
    public FailedSagasHandler(final SagaRepository<T> sagaRepository,
                              final EventStore eventStore, String... handleMethods) {
        this.sagaRepository = sagaRepository;
        this.eventStore = eventStore;

        if (handleMethods.length > 0) {
            this.handleMethods = handleMethods;
        }
        else {
            this.handleMethods = new String[]{"handle"};
        }
    }

    public static AssociationValue getSeqNumAssociation(long seqNum) {
        return new AssociationValue(RETRY_SEQ_NUM_KEY, String.valueOf(seqNum));
    }

    public static AssociationValue getAggregateIdAssociation(String aggreateId) {
        return new AssociationValue(RETRY_AGGREGATE_ID_KEY, aggreateId);
    }

    public void execute(Consumer<T> consumer) {
        sagaRepository.find(RETRY_ASSOCIATION)
                .forEach(sagaId -> DefaultUnitOfWork.startAndGet(null)
                        .execute(() -> {
                            final Saga<T> sagaWrapper = sagaRepository.load(sagaId);

                            sagaWrapper.execute(saga -> {
                                consumer.accept(saga);

                                executeFailedEvent(sagaWrapper, saga);
                            });
                        }));
    }

    public void executeFailedEvent(final Saga<T> sagaWrapper, final T saga) {
        FailedSagasHandler.RetryEvent retryEvent = getEventToRetry(sagaWrapper);

        if (retryEvent != null) {
            DomainEventStream stream = eventStore.readEvents(retryEvent.getAggregateId(), retryEvent.getSeqNum());

            if (stream.hasNext()) {
                DomainEventMessage<?> message = stream.next();

                Exception error = null;

                for (String handleMethod : handleMethods) {
                    try {
                        Method method = saga.getClass().getMethod(handleMethod, message.getPayloadType());

                        method.invoke(saga, message.getPayload());

                        sagaWrapper.getAssociationValues().remove(RETRY_ASSOCIATION);

                        error = null;

                        break;
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        error = e;
                    }
                }

                if (error != null) {
                    LOG.warn("Failed to retry saga {}: ", sagaWrapper.getSagaIdentifier(), error);
                }
            }
        }
    }

    private RetryEvent getEventToRetry(Saga<T> sagaWrapper) {
        RetryEvent retryEvent = new RetryEvent();

        sagaWrapper.getAssociationValues().forEach(value -> {
            if (value.getKey().equals(RETRY_AGGREGATE_ID_KEY)) {
                retryEvent.setAggregateId(value.getValue());
            }
            else if (value.getKey().equals(RETRY_SEQ_NUM_KEY)) {
                retryEvent.setSeqNum(Long.parseLong(value.getValue()));
            }
            else if (value.getKey().equals(RETRY_KEY)) {
                retryEvent.setActive(true);
            }
        });

        if (retryEvent.isActive() && retryEvent.getAggregateId() != null && retryEvent.getSeqNum() != null) {
            return retryEvent;
        }

        return null;
    }

    public static class RetryEvent {
        private String aggregateId;

        private Long seqNum;

        private boolean active;

        public RetryEvent() { }

        public void setAggregateId(final String aggregateId) {
            this.aggregateId = aggregateId;
        }

        public void setSeqNum(final Long seqNum) {
            this.seqNum = seqNum;
        }

        public String getAggregateId() {
            return aggregateId;
        }

        public Long getSeqNum() {
            return seqNum;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(final boolean active) {
            this.active = active;
        }
    }
}
