package is.symphony.chess.configuration;


import is.symphony.chess.board.repositories.BoardRepository;
import is.symphony.chess.game.repositories.GameRepository;
import is.symphony.chess.game.repositories.PairGameRepository;
import is.symphony.chess.game.repositories.RequestRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = {
        GameRepository.class, PairGameRepository.class,
        BoardRepository.class, RequestRepository.class})
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {
    @Override
    protected String getDatabaseName() {
        return "chess";
    }
}
