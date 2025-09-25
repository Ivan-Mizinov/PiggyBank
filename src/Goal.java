import java.time.LocalDate;

public class Goal {
    private String name;
    private double balance;
    private double target;
    private Category category;
    boolean isCompleted;
    private LocalDate endDate;
    private boolean notificationSent = false;

    public Goal(String name, Category category, double target, double balance, LocalDate endDate) {
        this.name = name;
        this.target = target;
        this.category = category;
        this.endDate = endDate;
        this.balance = balance;
        this.isCompleted = balance >= target;
        category.addGoal(this);
    }

    public void setBalance(double balance) {
        this.balance = balance;
        checkAndNotifyProgress();
    }

    public void checkAndNotifyProgress() {
        double progress = getProgressPercentage();
        if (progress >= 80 && !notificationSent) {
            System.out.println("\n🎉🎉🎉 Поздравляем! Цель '" + name + "' достигла 80% прогресса! 🎉🎉🎉\n");
            notificationSent = true;
        }
    }

    public void resetNotification() {
        notificationSent = false;
    }
    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getTarget() {
        return target;
    }

    public double getBalance() {
        return balance;
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
