package is.symphony.chess.game.core.queries;

import java.util.UUID;

public class GetRequestQuery {

    private UUID requestId;

    public GetRequestQuery() {}

    public GetRequestQuery(final UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }
}
