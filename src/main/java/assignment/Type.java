package assignment;

import java.math.BigDecimal;
import java.util.function.Function;

public enum Type {

    COMMON(Type::commonStockDividend),

    PREFERRED(Type::preferredStockDividend);

    private final Function<Stock, BigDecimal> dividendCalculator;

    private Type(Function<Stock, BigDecimal> dividendCalculator) {
        this.dividendCalculator = dividendCalculator;
    }

    public BigDecimal computeDividendValue(Stock stock) {
        return dividendCalculator.apply(stock);
    }

    private static BigDecimal commonStockDividend(Stock stock) {
        if (stock.getLastDividendValue() == null) {
            throw new IllegalStateException("Last dividend value must be set before calculating common stock dividend");
        }
        return stock.getLastDividendValue();
    }

    private static BigDecimal preferredStockDividend(Stock stock) {
        if (stock.getFixedDividendRate() == null) {
            throw new IllegalStateException("Fixed dividend rate must be set before calculating preferred stock dividend");
        }
        if (stock.getParValue() == null) {
            throw new IllegalStateException("Par value must be set before calculating preferred stock dividend");
        }
        return stock.getFixedDividendRate().multiply(stock.getParValue());
    }

}
