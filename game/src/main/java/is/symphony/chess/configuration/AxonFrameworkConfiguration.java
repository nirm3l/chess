package is.symphony.chess.configuration;

import is.symphony.chess.FailedSagasHandler;
import is.symphony.chess.game.saga.ChessBoardSaga;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.saga.AnnotatedSaga;
import org.axonframework.modelling.saga.SagaRepository;
import org.axonframework.modelling.saga.repository.AnnotatedSagaRepository;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AxonFrameworkConfiguration {

    @Bean
    public SagaRepository<ChessBoardSaga> mySagaRepository(SagaStore sagaStore) {
        return AnnotatedSagaRepository.builder()
                .sagaStore(sagaStore)
                .sagaType(ChessBoardSaga.class)
                .build();
    }

    @Bean
    public ConfigurerModule listenerInvocationErrorHandlerConfigurer() {
        return configurer -> configurer.
                eventProcessing(eventProcessingConfigurer ->
                        eventProcessingConfigurer
                                .registerListenerInvocationErrorHandler(
                                        "ChessBoardSagaProcessor", conf -> (exception, event, eventHandler) -> {
                                            if (event instanceof GenericDomainEventMessage && eventHandler instanceof AnnotatedSaga) {
                                                AnnotatedSaga<?> handler = ((AnnotatedSaga<?>) eventHandler);

                                                handler.getAssociationValues().add(FailedSagasHandler
                                                        .getSeqNumAssociation(((GenericDomainEventMessage<?>) event).getSequenceNumber()));
                                                handler.getAssociationValues().add(FailedSagasHandler
                                                        .getAggregateIdAssociation(((GenericDomainEventMessage<?>) event).getAggregateIdentifier()));
                                                handler.getAssociationValues().add(FailedSagasHandler.RETRY_ASSOCIATION);
                                            }
                                        }));
    }

    @Bean
    public FailedSagasHandler<ChessBoardSaga> failedSagasHandler(
            final SagaRepository<ChessBoardSaga> sagaRepository, final EventStore eventStore) {
        return new FailedSagasHandler<>(sagaRepository, eventStore);
    }
}
