package is.symphony.chess.engine.configuration;

import is.symphony.chess.engine.utils.ChessUCI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UCIConfiguration {

    @Bean
    public ChessProvider chessProvider(UCIProperties uciProperties) {
        return () -> {
            final ChessUCI chessUCI = new ChessUCI();
            chessUCI.start("telnet", uciProperties.getHost(), uciProperties.getPort());

            return chessUCI;
        };
    }
}
