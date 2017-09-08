package assignment.ex;

public class StockNotFoundException extends Exception {

    private static final long serialVersionUID = -3953264814924951383L;

    public StockNotFoundException() {
        super("Stock not found");
    }

}
