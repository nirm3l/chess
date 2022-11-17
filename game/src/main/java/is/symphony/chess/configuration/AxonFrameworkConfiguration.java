package is.symphony.chess.configuration;

import is.symphony.chess.game.saga.ChessBoardSaga;
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
}
