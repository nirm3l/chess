package is.symphony.chess.game.repositories;

import is.symphony.chess.game.models.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, UUID> {
}
