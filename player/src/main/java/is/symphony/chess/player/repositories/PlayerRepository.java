package is.symphony.chess.player.repositories;

import is.symphony.chess.player.models.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepository extends ReactiveMongoRepository<Player, UUID> {
}
