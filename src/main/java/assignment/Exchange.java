package assignment;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import assignment.ex.DuplicateStockException;
import assignment.ex.StockNotFoundException;

public class Exchange {

    private Map<Key, Stock> stocks;

    private static class Key {

        private final Type type;
        private final Symbol symbol;

        public Key(Type type, Symbol symbol) {
            super();
            this.type = type;
            this.symbol = symbol;
        }

        @Override
        public int hashCode() {
            final int prime = 1682612609;
            int result = 1;
            result = prime * result + (type == null ? 0 : type.hashCode());
            result = prime * result + (symbol == null ? 0 : symbol.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            if (type != other.type) {
                return false;
            }
            if (symbol == null) {
                if (other.symbol != null) {
                    return false;
                }
            } else if (!symbol.equals(other.symbol)) {
                return false;
            }
            return true;
        }
    }

    public Exchange() {
        stocks = new HashMap<>();
    }

    public void addStock(Stock stock) throws DuplicateStockException {
        if (stocks.containsKey(new Key(stock.getType(), stock.getSymbol()))) {
            throw new DuplicateStockException();
        }
        stocks.put(new Key(stock.getType(), stock.getSymbol()), stock);
    }

    public Stock getStock(Type type, Symbol symbol) throws StockNotFoundException {
        Stock stock = stocks.get(new Key(type, symbol));
        if (stock == null) {
            throw new StockNotFoundException();
        }
        return stock;
    }

    public List<Stock> getStocks() {
        return stocks.values().stream().collect(toList());
    }

    public BigDecimal computeFiveMinutesGBCEAllShareIndex() {
        return computeGBCEAllShareIndex(Instant.now().minus(Duration.ofMinutes(5)));
    }

    public BigDecimal computeGBCEAllShareIndex(Instant since) {
        BigDecimal product = stocks.values().stream()
            .map(s -> s.computeVolumeWeightedPrice(since))
            .collect(reducing(BigDecimal.ONE, (a, b) -> a.multiply(b, Math.CONTEXT)));
        return Math.nthRoot(stocks.size(), product);
    }

}
