package is.symphony.chess.game.core.queries;

import java.util.UUID;

public class RequestLiveUpdatesQuery {

    private UUID requestId;

    public RequestLiveUpdatesQuery() { }
    public RequestLiveUpdatesQuery(final UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(final UUID requestId) {
        this.requestId = requestId;
    }
}
