package assignment.ex;

public class DuplicateStockException extends Exception {

    private static final long serialVersionUID = -4213580511169845550L;

    public DuplicateStockException() {
        super("Duplicate stock");
    }

}
