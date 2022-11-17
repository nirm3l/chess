package is.symphony.chess;

import is.symphony.chess.game.core.models.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface GameRepository extends ReactiveMongoRepository<Game, UUID> {
}
