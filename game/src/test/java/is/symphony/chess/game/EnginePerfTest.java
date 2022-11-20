package is.symphony.chess.game;

import is.symphony.chess.game.core.commands.CreateGameCommand;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

class EnginePerfTest {

    private final List<Player> players = new ArrayList<>();

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    public static void main(String[] args) {
        EnginePerfTest test = new EnginePerfTest();
        test.createPlayers();

        Flux.range(1, 1000)
                        .flatMap(i -> test.createRandomGameAndWaitToFinish(100, 3)
                                .doOnNext(game ->
                                        System.out.println("Game " + game.getGameId() + " result: " + game.getResult())))
                .then().block();
    }

    private Mono<Game> createRandomGameAndWaitToFinish(int minutes, int incrementSeconds) {
        Tuple2<Player, Player> playersToPlay = getRandomPlayers();

        final CreateGameCommand createGameCommand = new CreateGameCommand();
        createGameCommand.setMinutes(minutes);
        createGameCommand.setIncrementSeconds(incrementSeconds);
        createGameCommand.setPieceColor(CreateGameCommand.PieceColor.WHITE);
        createGameCommand.setInvitePlayerId(playersToPlay.getT1().getPlayerId());

        return webClient.post()
                .uri("/games")
                .header("User", playersToPlay.getT2().getPlayerId().toString())
                .bodyValue(createGameCommand)
                .retrieve()
                .bodyToMono(Game.class)
                .flatMap(game -> acceptInvite(game, createGameCommand.getInvitePlayerId()))
                .flatMap(this::waitForFinish);
    }

    private Tuple2<Player, Player> getRandomPlayers() {
        Tuple2<Player, Player> result = Tuples.of(
                players.get(new Random().nextInt(players.size())), players.get(new Random().nextInt(players.size())));

        if (result.getT1().equals(result.getT2())) {
            return getRandomPlayers();
        }

        return result;
    }

    private Mono<Game> acceptInvite(final Game game, final UUID playerId) {
        return Mono.delay(Duration.ofSeconds(1)).then(Mono.defer(() -> webClient.post()
                .uri("/games/{0}/accept-invite", game.getGameId())
                .header("User", playerId.toString())
                .retrieve().bodyToMono(Void.class).thenReturn(game)));
    }

    private Mono<Game> waitForFinish(final Game game) {
        return webClient.get()
                .uri("/games/{0}/live-updates", game.getGameId())
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<Game>>() {})
                .filter(event -> event.data() != null && event.data().getResult() != null)
                .next().mapNotNull(ServerSentEvent::data);
    }

    private void createPlayers() {
        Flux.range(1, 5)
                .flatMap(i -> Flux.just("White Knight " + i, "Black Knight " + i)
                        .flatMap(name -> webClient.post()
                                .uri("/players")
                                .bodyValue(createBotPlayer(name, i))
                                .retrieve()
                                .bodyToMono(Player.class)
                                .onErrorResume(e -> {
                                    Player existingPlayer = new Player();
                                    existingPlayer.setPlayerId(
                                            UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)));

                                    return Mono.just(existingPlayer);
                                })
                        )).collectList().blockOptional()
                .ifPresent(players::addAll);
    }

    private Player createBotPlayer(String name, Integer level) {
        Player player = new Player();
        player.setName(name);
        player.setBot(true);
        player.setLevel(level);

        return player;
    }
}
