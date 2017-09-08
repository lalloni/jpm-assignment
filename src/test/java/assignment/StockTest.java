package assignment;

import static assignment.Side.BUY;
import static assignment.Side.SELL;
import static assignment.Type.COMMON;
import static assignment.Type.PREFERRED;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class StockTest {

    private static final String MIN = "0.005";

    @Test
    public void testAddTrades() {
        Stock stock = new Stock(COMMON, new Symbol("XXX"));
        Instant t0 = Instant.now();

        stock.recordTrade(new Trade(t0, BUY, 10, ONE));
        stock.recordTrade(new Trade(t0.plusMillis(1), SELL, 5, ONE));
        stock.recordTrade(new Trade(t0, SELL, 6, ONE));
    }

    @Property
    public void computePriceEarningsRatio(Type type, @InRange(min = MIN) BigDecimal lastDividend,
        @InRange(min = MIN) BigDecimal price) {
        Stock stock = new Stock(type, new Symbol("XXX"));
        stock.setLastDividendValue(lastDividend);
        assertThat(stock.computePriceEarningsRatio(price)).isEqualTo(price.divide(lastDividend, Math.CONTEXT));
    }

    @Property
    public void commonStockDividendYield(@InRange(min = MIN) BigDecimal lastDividend,
        @InRange(min = MIN) BigDecimal price) {
        Stock commonStock = new Stock(COMMON, new Symbol("XXX"));
        commonStock.setLastDividendValue(lastDividend);
        assertThat(commonStock.computeDividendYield(price))
            .isEqualTo(lastDividend.divide(price, Math.CONTEXT));
    }

    @Property
    public void preferredStockDividendYield(@InRange(min = MIN) BigDecimal fixedDividendRate,
        @InRange(min = MIN) BigDecimal parValue, @InRange(min = MIN) BigDecimal price) {
        Stock preferredStock = new Stock(PREFERRED, new Symbol("XXX"));
        preferredStock.setFixedDividendRate(fixedDividendRate);
        preferredStock.setParValue(parValue);
        assertThat(preferredStock.computeDividendYield(price))
            .isEqualTo(fixedDividendRate.multiply(parValue).divide(price, Math.CONTEXT));
    }

    @Test
    public void volumeWeightedStockPrice() {
        Stock stock = new Stock(COMMON, new Symbol("XXX"));

        TestUtils.generateTrades(1000, 100, Instant.now(), stock);

        List<Trade> allTrades = stock.getTrades();

        Instant lastTradeInstant = allTrades.get(allTrades.size() - 1).getInstant();

        Instant fiveMinutesBeforeLast = lastTradeInstant.minus(Duration.ofMinutes(5));

        List<Trade> trades = allTrades
            .stream()
            .filter(t -> !t.getInstant().isBefore(fiveMinutesBeforeLast))
            .collect(toList());

        BigDecimal totalSum = trades.stream()
            .collect(reducing(ZERO, t -> t.getPrice().multiply(BigDecimal.valueOf(t.getQuantity())), BigDecimal::add));

        long quantitySum = trades.stream()
            .collect(reducing(0l, Trade::getQuantity, (a, b) -> a + b));

        BigDecimal volumeWeightedPrice = totalSum.divide(BigDecimal.valueOf(quantitySum), Math.CONTEXT);

        assertThat(stock.computeVolumeWeightedPrice(fiveMinutesBeforeLast))
            .isEqualTo(volumeWeightedPrice);

    }

}
