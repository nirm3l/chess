package is.symphony.chess.game.saga;

import is.symphony.chess.game.core.commands.CancelPlayerPairingRequestCommand;
import is.symphony.chess.game.core.commands.PairPlayerWithGameCommand;
import is.symphony.chess.game.core.events.GamePairedEvent;
import is.symphony.chess.game.core.events.PlayerPairingCanceledEvent;
import is.symphony.chess.game.core.events.QuickPairRequestedEvent;
import is.symphony.chess.game.core.exceptions.GameAlreadyPairedException;
import is.symphony.chess.game.core.exceptions.NoGameAvailableException;
import is.symphony.chess.game.core.queries.FindGameToPairQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
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

    private static final Logger LOG = LoggerFactory.getLogger(QuickPairGameSaga.class);

    public static final String REQUEST_ID_ASSOCIATION = "requestId";

    public static final String PLAYER_ID_ASSOCIATION = "playerId";

    public static final String GAME_ID_ASSOCIATION = "gameId";

    private transient CommandGateway commandGateway;

    private transient ReactorQueryGateway queryGateway;

    private UUID playerId;

    @Autowired
    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Autowired
    public void setQueryGateway(final ReactorQueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = REQUEST_ID_ASSOCIATION)
    public void handle(QuickPairRequestedEvent quickPairRequestedEvent) {
        playerId = quickPairRequestedEvent.getPlayerId();

        SagaLifecycle.associateWith(PLAYER_ID_ASSOCIATION, playerId.toString());

        while (true) {
            final UUID gameId = queryGateway.query(
                    new FindGameToPairQuery(
                            quickPairRequestedEvent.getPlayerId(), quickPairRequestedEvent.getMinutes(),
                            quickPairRequestedEvent.getIncrementSeconds()), UUID.class)
                    .switchIfEmpty(Mono.error(new NoGameAvailableException()))
                    .retryWhen(Retry.fixedDelay(60, Duration.ofSeconds(1)))
                    .onErrorResume(e -> {
                        LOG.warn("Cancelling pair player with game request", e);

                        return Mono.empty();
                    })
                    .block();

            if (gameId != null) {
                SagaLifecycle.associateWith(GAME_ID_ASSOCIATION, gameId.toString());

                try {
                    commandGateway.sendAndWait(new PairPlayerWithGameCommand(gameId, playerId, quickPairRequestedEvent.getRequestId()));

                    // Break while loop in case player is paired with game
                    break;
                }
                catch (GameAlreadyPairedException e) {
                    SagaLifecycle.removeAssociationWith(GAME_ID_ASSOCIATION, gameId.toString());
                }
            }
            else {
                commandGateway.sendAndWait(new CancelPlayerPairingRequestCommand(quickPairRequestedEvent.getRequestId()));

                break;
            }
        }
    }

    @SagaEventHandler(associationProperty = GAME_ID_ASSOCIATION)
    public void handle(GamePairedEvent gamePairedEvent) {
        if (gamePairedEvent.getPlayerId().equals(playerId)) {
            SagaLifecycle.end();
        }
    }

    @SagaEventHandler(associationProperty = REQUEST_ID_ASSOCIATION)
    public void handle(PlayerPairingCanceledEvent playerPairingCanceledEvent) {
        SagaLifecycle.end();
    }
}
