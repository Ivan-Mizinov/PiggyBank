import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Goal {
    private String name;
    private Category category;
    private double balance;
    private double target;
    boolean isCompleted;
    private LocalDate endDate;
    private boolean notificationSent = false;
    private List<Deposit> deposits = new ArrayList<>();

    public Goal(String name, Category category, double target, double balance, LocalDate endDate) {
        this.name = name;
        this.target = target;
        this.category = category;
        this.endDate = endDate;
        this.balance = balance;
        this.isCompleted = balance >= target;
        category.addGoal(this);
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public void addDeposit(double amount, LocalDate date) {
        deposits.add(new Deposit(amount, date));
    }

    public void setBalance(double amount, LocalDate date) {
        this.balance += amount;
        deposits.add(new Deposit(amount, date));
        checkAndNotifyProgress();
    }

    public void checkAndNotifyProgress() {
        double progress = getProgressPercentage();
        if (progress >= 80 && !notificationSent) {
            System.out.println("\n🎉🎉🎉 Поздравляем! Цель '" + name + "' достигла 80% прогресса! 🎉🎉🎉\n");
            notificationSent = true;
        }
        if (progress < 80) {
            notificationSent = false;
        }
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

    public double getProgressPercentage() {
        return (balance / target) * 100;
    }

    @Override
    public String toString() {
        return "\nЦель: " + name +
                "\nЦелевая сумма: " + target +
                "\nТекущий баланс: " + balance +
                "\nПрогресс: " + String.format("%.2f%%", getProgressPercentage()) +
                "\nСтатус: " + (isCompleted ? "Выполнена" : "Не выполнена") +
                "\nДата завершения: " + endDate;
    }

    public boolean needsReminder() {
        boolean reminderEnabled = true;
        if (!reminderEnabled) return false;
        LocalDate today = LocalDate.now();
        return today.isAfter(endDate.minusDays(10)) && today.isBefore(endDate);
    }

    public LocalDate predictCompletionDate() {
        if (balance >= target) return LocalDate.now();

        // Если нет депозитов, возвращаем null
        if (deposits.isEmpty()) return null;

        // Получаем все даты депозитов и сортируем по возрастанию
        List<LocalDate> depositDates = deposits.stream()
                .map(Deposit::getDate).sorted().toList();

        // Вычисляем общее количество дней между первым и последним депозитом
        long totalDays = ChronoUnit.DAYS.between(
                depositDates.getFirst(),
                depositDates.getLast());

        // Вычисляем сколько в сумме было вложено депозитами
        double sum = deposits.stream()
                .mapToDouble(Deposit::getAmount).sum();

        // Если всего один депозит, возвращаем null
        if (totalDays == 0) return null;

        // Вычисляем среднюю сумму пополнения в день
        double averageDailyDeposit = sum / (totalDays + 1);

        // Вычисляем оставшуюся сумму
        double remainingAmount = target - balance;

        // Вычисляем предполагаемое количество дней до завершения
        long predictedDays = Math.round(remainingAmount / averageDailyDeposit);

        return LocalDate.now().plusDays(predictedDays);
    }

    public String getPredictionInfo() {
        LocalDate predictedDate = predictCompletionDate();
        if (predictedDate == null) {
            return "Недостаточно данных для прогноза";
        }

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), predictedDate);
        return String.format(
                "Предполагаемая дата завершения: %s (через %d дней)",
                predictedDate,
                daysLeft
        );
    }
}
