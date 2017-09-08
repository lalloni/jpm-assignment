package assignment;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class Stock {

    private final Type type;

    private final Symbol symbol;

    private BigDecimal lastDividendValue;

    private BigDecimal fixedDividendRate;

    private BigDecimal parValue;

    private TreeMap<Instant, List<Trade>> trades = new TreeMap<>();

    private static class Pair {

        private BigDecimal a;
        private BigDecimal b;

        public static final Pair ZERO = new Pair(BigDecimal.ZERO, BigDecimal.ZERO);

        private Pair(BigDecimal a, BigDecimal b) {
            this.a = a;
            this.b = b;
        }

        public Pair add(Pair other) {
            return new Pair(a.add(other.a, Math.CONTEXT), b.add(other.b, Math.CONTEXT));
        }

        public BigDecimal quotient() {
            return a.divide(b, Math.CONTEXT);
        }

        public static Pair forTotal(Trade trade) {
            BigDecimal quantity = BigDecimal.valueOf(trade.getQuantity());
            return new Pair(trade.getPrice().multiply(quantity, Math.CONTEXT), quantity);
        }

    }

    public Stock(Type type, Symbol symbol) {
        super();
        this.type = type;
        this.symbol = symbol;
    }

    public Type getType() {
        return type;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public BigDecimal getLastDividendValue() {
        return lastDividendValue;
    }

    public void setLastDividendValue(BigDecimal value) {
        lastDividendValue = value;
    }

    public BigDecimal getFixedDividendRate() {
        return fixedDividendRate;
    }

    public void setFixedDividendRate(BigDecimal rate) {
        fixedDividendRate = rate;
    }

    public BigDecimal getParValue() {
        return parValue;
    }

    public void setParValue(BigDecimal value) {
        parValue = value;
    }

    public List<Trade> getTrades() {
        return trades.values().stream().flatMap(List::stream).collect(toList());
    }


    /**
     * Computes the dividend yield of this {@link Stock} for the price given in
     * the {@link price} parameter taking into account whether the stock is
     * COMMON or PREFERRED as specified in Table 2 Dividend Yield Formula. <br>
     * Satisfies requirement point 2.a.i.
     */
    public BigDecimal computeDividendYield(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price must not be null for calculating Dividend Yield");
        }
        if (BigDecimal.ZERO.equals(price)) {
            throw new IllegalArgumentException("Price must not be zero for calculating Dividend Yield");
        }
        return type.computeDividendValue(this).divide(price, Math.CONTEXT);
    }

    /**
     * Computes the P/E ratio for the given {@link price} parameter using the
     * last dividend value recorded for the stock. <br>
     * Satisfies requirement point 2.a.ii.
     */
    public BigDecimal computePriceEarningsRatio(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price must not be null for calculating P/E Ratio");
        }
        if (lastDividendValue == null) {
            throw new IllegalStateException("Last dividend value must be set before calculating P/E Ratio");
        }
        if (BigDecimal.ZERO.equals(lastDividendValue)) {
            throw new IllegalStateException("Last dividend value must not be zero for calculating P/E Ratio");
        }
        return price.divide(lastDividendValue, Math.CONTEXT);
    }

    /**
     * Records the {@link Trade} given in the {@link Trade} parameter into this
     * {@link Stock} trade history. <br>
     * Satisfies requirement point 2.a.iii.
     */
    public void recordTrade(Trade trade) {
        trades.computeIfAbsent(trade.getInstant(), t -> new LinkedList<>()).add(trade);
    }

    /**
     * Computes the Volume Weighted Stock Price value based on the trades
     * recorded in the last five minutes. <br>
     * Satisfies requirement point 2.a.iv.
     */
    public BigDecimal computeFiveMinuteVolumeWeightedPrice() {
        return computeVolumeWeightedPrice(Instant.now().minus(Duration.ofMinutes(5)));
    }

    /**
     * Computes the Volume Weighted Stock Price value based on the trades
     * recorded since the instant provided in the {@link start} parameter using
     * the formula specified in Table 2 Volume Weighted Stock Price Formula.
     * <br>
     * Generalizes {@link #computeFiveMinuteVolumeWeightedPrice()} on time.
     */
    public BigDecimal computeVolumeWeightedPrice(Instant start) {
        if (start == null) {
            throw new IllegalArgumentException("Start instant must not be null for calculating the Volume Weighted Price");
        }
        return trades.tailMap(start).values().stream()
            .flatMap(List::stream)
            .collect(reducing(Pair.ZERO, Pair::forTotal, Pair::add))
            .quotient();
    }

}
