package is.symphony.chess.game;

import com.github.bhlangonijr.chesslib.move.MoveList;
import is.symphony.chess.game.core.commands.CreateGameCommand;
import is.symphony.chess.game.core.commands.PlayerMoveCommand;
import is.symphony.chess.game.models.Game;
import is.symphony.chess.player.models.Player;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

class PerfTest {

    private final static String PGN = "1. e4 e6 2. d4 d5 3. Nd2 c5 4. Ngf3 Nc6 5. e4xd5 e6xd5 6. Bb5 Bd7 7. O-O cxd4 8. Nb3 Nf6 9. Nbxd4 Nxd4 10. Nxd4 Be7 11. Bd3 O-O 12. h3 Bc5 13. c3 Bxd4 14. cxd4 Re8 15. Bg5 Qb6 16. Bxf6 Qxf6 17. Qh5 h6 18. Qxd5 Bc6 19. Qf5 Qxf5 20. Bxf5 Rad8 21. Rfd1 Rd6 22. Rd2 Red8 23. Rad1 g6 24. Bc2 a5 25. a3 a4 26. f3 b5 27. Kf2 f5 28. h4 h5 29. Kg3 Kf7 30. Kf4 Kf6 31. Bb1 Bd5 32. Bd3 Bb3 33. Bc2 Bxc2 34. Rxc2 Rxd4+ 35. Rxd4 Rxd4+ 36. Kg3 f4+ 37. Kf2 g5 38. hxg5+ Kxg5 39. Rc5+ Kh4 40. Rxb5 Rd2+ 41. Kf1 Kg3 42. Rg5+ Kh4 43. Rb5 Kg3 44. Rg5+ Kh4 45. Rb5";

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    public static void main(String[] args) {
        PerfTest test = new PerfTest();

        Tuple2<Player, Player> players = test.createPlayers();

        int size = 1000;

        List<Game> games = Flux.range(1, size)
                .flatMap(i -> test.createRandomGameAndWaitToFinish(players))
                .doOnNext(game -> {
                    assert game.getResult() != null;
                }).collectList().block();

        assert games != null && games.size() == size;
    }

    private Mono<Game> createRandomGameAndWaitToFinish(Tuple2<Player, Player> players) {
        final CreateGameCommand createGameCommand = new CreateGameCommand();
        createGameCommand.setMinutes(100);
        createGameCommand.setIncrementSeconds(0);
        createGameCommand.setPieceColor(CreateGameCommand.PieceColor.WHITE);
        createGameCommand.setInvitePlayerId(players.getT2().getPlayerId());

        final MoveList moveList = new MoveList();
        moveList.loadFromSan(PGN);

        return webClient.post()
                .uri("/games")
                .header("User", players.getT1().getPlayerId().toString())
                .bodyValue(createGameCommand)
                .retrieve()
                .bodyToMono(Game.class)
                .flatMap(game -> acceptInvite(game, createGameCommand.getInvitePlayerId()))
                .flatMap(this::getGameWithBoardInfo)
                .flatMap(game -> Flux.fromIterable(moveList)
                        .index()
                        .flatMap(moveTuple -> {
                            final PlayerMoveCommand playerMoveCommand = new PlayerMoveCommand();
                            playerMoveCommand.setMove(moveTuple.getT2().toString());

                            if (moveTuple.getT1() % 2 == 0) {
                                return playMove(game, players.getT1().getPlayerId(), playerMoveCommand).then();
                            }
                            else {
                                return playMove(game, players.getT2().getPlayerId(), playerMoveCommand).then();
                            }
                        } ,1)
                        .then(Mono.defer(() -> Flux.just(players.getT1(), players.getT2())
                                .flatMap(player -> draw(game, player.getPlayerId())).collectList()))
                        .then(Mono.defer(() -> getGameWithResult(game))));
    }

    private Mono<Void> draw(final Game game, final UUID playerId) {
        return Mono.defer(() -> webClient.post()
                .uri("/games/{0}/draw", game.getGameId())
                .header("User", playerId.toString())
                .retrieve().bodyToMono(Void.class));
    }

    private Mono<Void> playMove(final Game game, final UUID playerId, final PlayerMoveCommand command) {
        return Mono.defer(() -> webClient.post()
                .uri("/games/{0}/play", game.getGameId())
                .header("User", playerId.toString())
                .bodyValue(command)
                .retrieve().bodyToMono(Void.class));
    }

    private Mono<Game> acceptInvite(final Game game, final UUID playerId) {
        return Mono.delay(Duration.ofSeconds(1)).then(Mono.defer(() -> webClient.post()
                .uri("/games/{0}/accept-invite", game.getGameId())
                .header("User", playerId.toString())
                .retrieve().bodyToMono(Void.class).thenReturn(game)));
    }

    private Flux<ServerSentEvent<Game>> getGameEvents(final Game game) {
        return webClient.get()
                .uri("/games/{0}/live-updates", game.getGameId())
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<>() {});
    }

    private Mono<Game> getGameWithBoardInfo(final Game game) {
        return getGameEvents(game)
                .filter(event -> event.data() != null &&
                        event.data().getBoardId() != null).mapNotNull(ServerSentEvent::data).next();
    }

    private Mono<Game> getGameWithResult(final Game game) {
        return getGameEvents(game)
                .filter(event -> event.data() != null &&
                        event.data().getResult() != null).mapNotNull(ServerSentEvent::data).next();
    }

    private Tuple2<Player, Player> createPlayers() {
        return Flux.just("White Knight", "Black Knight")
                        .flatMap(name -> webClient.post()
                                .uri("/players")
                                .bodyValue(createPlayer(name))
                                .retrieve()
                                .bodyToMono(Player.class)
                                .onErrorResume(e -> {
                                    Player existingPlayer = new Player();
                                    existingPlayer.setPlayerId(
                                            UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)));

                                    return Mono.just(existingPlayer);
                                })
                        ).collectList().map(players -> Tuples.of(players.get(0), players.get(1))).block();
    }

    private Player createPlayer(String name) {
        Player player = new Player();
        player.setName(name);

        return player;
    }
}
