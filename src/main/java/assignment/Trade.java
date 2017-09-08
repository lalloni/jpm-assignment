package assignment;

import java.math.BigDecimal;
import java.time.Instant;

public class Trade {

    private Instant instant;

    private Side side;

    private long quantity;

    private BigDecimal price;

    public Trade(Instant instant, Side side, long quantity, BigDecimal price) {
        super();
        this.instant = instant;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    public Instant getInstant() {
        return instant;
    }

    public Side getSide() {
        return side;
    }

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

}
