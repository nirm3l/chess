package is.symphony.chess.game.repositories;

import is.symphony.chess.game.models.PairGame;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface PairGameRepository extends ReactiveMongoRepository<PairGame, UUID> {
}
