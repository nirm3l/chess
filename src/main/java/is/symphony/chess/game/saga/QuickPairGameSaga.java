package is.symphony.chess.saga;

import is.symphony.chess.core.commands.CancelPlayerPairingCommand;
import is.symphony.chess.core.commands.PairPlayerWithGameCommand;
import is.symphony.chess.core.events.GamePairedEvent;
import is.symphony.chess.core.events.PlayerPairingCanceledEvent;
import is.symphony.chess.core.events.QuickPairRequestedEvent;
import is.symphony.chess.core.exceptions.GameAlreadyPairedException;
import is.symphony.chess.core.exceptions.NoGameAvailableException;
import is.symphony.chess.core.queries.FindGameToPairQuery;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Saga
public class QuickPairGameSaga {

    private final Logger logger = LoggerFactory.getLogger(QuickPairGameSaga.class);

    public static final String PLAYER_ID_ASSOCIATION = "playerId";

    public static final String GAME_ID_ASSOCIATION = "gameId";

    @Autowired
    private transient ReactorCommandGateway commandGateway;

    @Autowired
    private transient ReactorQueryGateway queryGateway;

    private UUID playerId;

    private boolean playerPaired;

    @StartSaga
    @SagaEventHandler(associationProperty = PLAYER_ID_ASSOCIATION)
    public void handle(QuickPairRequestedEvent quickPairRequestedEvent) {
        playerId = quickPairRequestedEvent.getPlayerId();

        while (true) {
            final UUID gameId = queryGateway.query(
                    new FindGameToPairQuery(
                            quickPairRequestedEvent.getPlayerId(), quickPairRequestedEvent.getMinutes(),
                            quickPairRequestedEvent.getIncrementSeconds()), UUID.class)
                    .switchIfEmpty(Mono.error(new NoGameAvailableException()))
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
                    .onErrorResume(e -> {
                        logger.warn("Cancelling pair player with game request", e);

                        return Mono.empty();
                    })
                    .block();

            if (gameId != null) {
                SagaLifecycle.associateWith(GAME_ID_ASSOCIATION, gameId.toString());

                try {
                    commandGateway.send(new PairPlayerWithGameCommand(gameId, playerId)).block();

                    // Break while loop in case player is paired with game
                    break;
                }
                catch (GameAlreadyPairedException e) {
                    SagaLifecycle.removeAssociationWith(GAME_ID_ASSOCIATION, gameId.toString());
                }
            }
            else {
                commandGateway.send(new CancelPlayerPairingCommand(playerId)).subscribe();

                break;
            }
        }
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GamePairedEvent gamePairedEvent) {
        if (playerId.equals(gamePairedEvent.getPlayerId())) {
            playerPaired = true;

            SagaLifecycle.end();
        }
    }

    @SagaEventHandler(associationProperty = PLAYER_ID_ASSOCIATION)
    public void handle(PlayerPairingCanceledEvent playerPairingCanceledEvent) {
        SagaLifecycle.end();
    }
}
