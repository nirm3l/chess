package is.symphony.chess.board.core.models;

public enum BoardGameResult {
    WIN_1_0("1-0"), WIN_0_1("0-1"), DRAW("1/2-1/2");

    private final String result;

    BoardGameResult(String value) {
        result = value;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result;
    }

    public static BoardGameResult valueOfResult(String result) {
        for (BoardGameResult e : values()) {
            if (e.result.equals(result)) {
                return e;
            }
        }
        return null;
    }
}
