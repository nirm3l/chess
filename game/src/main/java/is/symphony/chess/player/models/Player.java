package is.symphony.chess.player.models;

import java.util.UUID;

public class Player {
    private UUID playerId;

    private UUID engineId;

    private String name;

    private String email;

    private boolean bot;

    private Integer level;

    private Integer rating;

    private Long version;

    public Player() { }

    public Player(final UUID playerId) {
        this.playerId = playerId;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(final boolean bot) {
        this.bot = bot;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(final Integer level) {
        this.level = level;
    }

    public UUID getEngineId() {
        return engineId;
    }

    public void setEngineId(final UUID engineId) {
        this.engineId = engineId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }
}