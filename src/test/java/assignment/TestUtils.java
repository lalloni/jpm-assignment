package assignment;

import static assignment.Side.BUY;
import static assignment.Side.SELL;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

public class TestUtils {

    public static void generateTrades(int count, int maxQuantity, Instant starting, Stock stock) {
        Instant time = starting;
        Random r = new Random(System.nanoTime());
        for (int i = 0; i < count; i++) {
            time = time.plusMillis(r.nextInt(1000));
            Trade trade = new Trade(
                time,
                r.nextBoolean() ? BUY : SELL,
                1 + r.nextInt(maxQuantity),
                BigDecimal.valueOf(1 + 1000 * r.nextDouble()));
            stock.recordTrade(trade);
        }
    }

}
