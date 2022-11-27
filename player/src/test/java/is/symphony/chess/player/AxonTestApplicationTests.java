package is.symphony.chess.player;

import is.symphony.chess.player.core.commands.RegisterPlayerCommand;
import is.symphony.chess.player.core.commands.UpdateRatingCommand;
import is.symphony.chess.player.core.events.PlayerRatingUpdatedEvent;
import is.symphony.chess.player.core.events.PlayerRegisteredEvent;
import is.symphony.chess.player.handlers.PlayerAggregate;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class AxonTestApplicationTests {

    private FixtureConfiguration<PlayerAggregate> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(PlayerAggregate.class);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testRegisterPlayerCommand() {
        RegisterPlayerCommand registerPlayerCommand = new RegisterPlayerCommand(UUID.randomUUID());
        registerPlayerCommand.setName("test player");

        fixture.given()
                .when(registerPlayerCommand)
                .expectSuccessfulHandlerExecution()
                .expectEvents(new PlayerRegisteredEvent(
                        registerPlayerCommand.getPlayerId(),
                        registerPlayerCommand.getName(), null, false, null, 1500));
    }

    @Test
    public void testUpdateRatingCommand() {
        RegisterPlayerCommand registerPlayerCommand = new RegisterPlayerCommand(UUID.randomUUID());
        registerPlayerCommand.setName("test player");

        fixture.givenCommands(registerPlayerCommand)
                .when(new UpdateRatingCommand(registerPlayerCommand.getPlayerId(), 1510, 1L))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new PlayerRatingUpdatedEvent(registerPlayerCommand.getPlayerId(), 1510, 2L));
    }
}
