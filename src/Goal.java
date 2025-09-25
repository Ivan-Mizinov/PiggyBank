import java.time.LocalDate;

public class Goal {
    private String name;
    private double balance;
    private double target;
    private Category category;
    boolean isCompleted;
    private LocalDate endDate;

    public Goal(String name, Category category, double target, double balance, LocalDate endDate) {
        this.name = name;
        this.target = target;
        this.category = category;
        this.endDate = endDate;
        this.balance = balance;
        this.isCompleted = target >= balance;
        category.addGoal(this);
    }

    public double getTarget() {
        return target;
    }

    public double getBalance() {
        return balance;
    }

    public void updateBalance(double amount) {
        double newBalance = balance + amount;

        if (newBalance < 0) {
            System.out.println("Ошибка: баланс не может быть отрицательным!");
            return;
        }

        balance = newBalance;
        checkCompletion();
    }

    private void checkCompletion() {
        if (balance >= target) {
            isCompleted = true;
            System.out.println("Баланс превысил целевую сумму. Цель достигнута!");
        }
    }

    public double getProgressPercentage() {
        return (balance / target) * 100;
    }

    @Override
    public String toString() {
        return "Цель: " + name +
                "\nЦелевая сумма: " + target +
                "\nТекущий баланс: " + balance +
                "\nПрогресс: " + String.format("%.2f%%", getProgressPercentage()) +
                "\nКатегория: " + category.getName() +
                "\nСтатус: " + (isCompleted ? "Выполнена" : "Не выполнена") +
                "\nДата завершения: " + endDate;
    }
}
