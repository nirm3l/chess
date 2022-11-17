package is.symphony.chess.configuration;

import com.mongodb.client.MongoClient;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonFrameworkConfiguration {
    @Bean
    public SagaStore mySagaStore(MongoClient client, Serializer serializer) {
        return MongoSagaStore.builder()
                .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                .serializer(serializer)
                .build();
    }

    @Bean
    public SnapshotTriggerDefinition chessAggregateSnapshotTriggerDefinition(
            Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    public ConfigurerModule mongoTokenStoreConfigurer(MongoClient client, Serializer serializer) {
        return configurer -> configurer.
                eventProcessing(eventProcessingConfigurer ->
                        eventProcessingConfigurer.registerTokenStore(
                                conf -> MongoTokenStore.builder()
                                        .serializer(serializer)
                                        .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                                        .build()));
    }
}
