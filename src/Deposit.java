import java.time.LocalDate;

public class Deposit {
    private double amount;
    private LocalDate date;

    public Deposit(double amount, LocalDate date) {
        this.amount = amount;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }
}
