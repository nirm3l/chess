package is.symphony.chess.player.core.events;

import java.util.UUID;

public class PlayerRegisteredEvent {

    private UUID playerId;

    private String name;

    private String email;

    private boolean bot;

    public PlayerRegisteredEvent() { }

    public PlayerRegisteredEvent(final UUID playerId, final String name, final String email) {
        this.playerId = playerId;
        this.name = name;
        this.email = email;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final UUID playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(final boolean bot) {
        this.bot = bot;
    }
}
