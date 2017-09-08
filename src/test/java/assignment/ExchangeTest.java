package assignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Test;

import assignment.ex.DuplicateStockException;
import assignment.ex.StockNotFoundException;

public class ExchangeTest {

    @Test
    public void testAddStock() throws DuplicateStockException {
        Exchange e = new Exchange();
        e.addStock(new Stock(Type.COMMON, new Symbol("FOO")));
        e.addStock(new Stock(Type.PREFERRED, new Symbol("FOO")));
        e.addStock(new Stock(Type.COMMON, new Symbol("BAR")));
        assertThat(catchThrowable(() -> e.addStock(new Stock(Type.COMMON, new Symbol("FOO")))))
            .isInstanceOf(DuplicateStockException.class).hasMessage("Duplicate stock");
        assertThat(e.getStocks().size()).isEqualTo(3);
    }

    @Test
    public void testGetStock() throws DuplicateStockException, StockNotFoundException {
        Exchange e = new Exchange();
        Stock commonFoo = new Stock(Type.COMMON, new Symbol("FOO"));
        e.addStock(commonFoo);
        e.addStock(new Stock(Type.PREFERRED, new Symbol("FOO")));
        e.addStock(new Stock(Type.COMMON, new Symbol("BAR")));
        assertThat(e.getStock(Type.COMMON, new Symbol("FOO")))
            .isSameAs(commonFoo);
        assertThat(catchThrowable(() -> e.getStock(Type.COMMON, new Symbol("BAZ"))))
            .isInstanceOf(StockNotFoundException.class).hasMessage("Stock not found");
    }

    @Test
    public void testComputeGBCEAllShareIndex() throws DuplicateStockException {
        Instant starting = Instant.now();
        Exchange e = new Exchange();
        Stock foo = new Stock(Type.COMMON, new Symbol("FOO"));
        Stock bar = new Stock(Type.PREFERRED, new Symbol("BAR"));
        Stock baz = new Stock(Type.COMMON, new Symbol("BAZ"));
        e.addStock(foo);
        e.addStock(bar);
        e.addStock(baz);
        TestUtils.generateTrades(10000, 100, starting, foo);
        TestUtils.generateTrades(10000, 100, starting, bar);
        TestUtils.generateTrades(10000, 100, starting, baz);
        BigDecimal index = e.computeGBCEAllShareIndex(starting);
        BigDecimal fooVWP = foo.computeVolumeWeightedPrice(starting);
        BigDecimal barVWP = bar.computeVolumeWeightedPrice(starting);
        BigDecimal bazVWP = baz.computeVolumeWeightedPrice(starting);
        assertThat(index).isNotNull().isGreaterThan(BigDecimal.ZERO)
            .isEqualTo(Math.nthRoot(3, fooVWP.multiply(barVWP, Math.CONTEXT).multiply(bazVWP, Math.CONTEXT)));
    }

}
