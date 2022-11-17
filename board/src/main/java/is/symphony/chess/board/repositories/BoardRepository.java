package is.symphony.chess.board.repositories;

import is.symphony.chess.board.models.Board;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, UUID> {
}
