package is.symphony.chess.game.utils;

public class EloRating {

    private EloRating() {}

    public static int calculate(int player1Rating, int player2Rating, double score) {
        double exponent = (double) (player2Rating - player1Rating) / 400;
        double expectedOutcome = (1 / (1 + (Math.pow(10, exponent))));

        return (int) Math.round(player1Rating + 16 * (score - expectedOutcome));
    }
}
