package is.symphony.chess;

import com.mongodb.client.MongoClient;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.serialization.Serializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class AxonStarterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SagaStore mySagaStore(MongoClient client, Serializer serializer) {
        return MongoSagaStore.builder()
                .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                .serializer(serializer)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public SnapshotTriggerDefinition aggregateSnapshotTriggerDefinition(
            Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurerModule mongoTokenStoreConfigurer(MongoClient client, Serializer serializer) {
        return configurer -> configurer.
                eventProcessing(eventProcessingConfigurer ->
                        eventProcessingConfigurer.registerTokenStore(
                                conf -> MongoTokenStore.builder()
                                        .serializer(serializer)
                                        .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                                        .build()));
    }

    @Bean
    public CommandGateway commandGateway(CommandBus commandBus) {
        return DefaultCommandGateway.builder()
                .retryScheduler(IntervalRetryScheduler.builder()
                        .retryExecutor(Executors.newSingleThreadScheduledExecutor())
                        .build())
                .commandBus(commandBus).build();
    }
}
