package is.symphony.chess.engine.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public class RegisterEngineCommand {

    @TargetAggregateIdentifier
    private UUID engineId;

    private String name;

    private Integer level;

    public RegisterEngineCommand() {
    }

    public RegisterEngineCommand(final UUID engineId, final String name, final Integer level) {
        this.engineId = engineId;
        this.name = name;
        this.level = level;
    }

    public UUID getEngineId() {
        return engineId;
    }

    public void setEngineId(final UUID engineId) {
        this.engineId = engineId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(final Integer level) {
        this.level = level;
    }
}
