package is.symphony.chess.game.repositories;

import is.symphony.chess.game.models.Request;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RequestRepository extends ReactiveMongoRepository<Request, UUID> {
}
