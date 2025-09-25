import java.util.ArrayList;
import java.util.List;

public class PiggyBank {
    private List<Category> categories = new ArrayList<>();
    private FileManager fileManager = new FileManager();
    private static final double NOTIFICATION_THRESHOLD = 0.8;
    private boolean isNotificationSent = false;

    public void checkAndNotifyProgress() {
        double progress = calculateOverallProgress();
        double formattedProgress = Math.round(progress * 100.0) / 100.0;
        if (progress >= NOTIFICATION_THRESHOLD * 100 && !isNotificationSent) {
            System.out.println("\n🎉🎉🎉 Поздравляем! Вы достигли " + String.format("%.2f", formattedProgress) + "% от вашей цели! 🎉🎉🎉");
            System.out.println("🎉🎉🎉 Осталось совсем немного до достижения всех целей! 🎉🎉🎉\n");
            isNotificationSent = true;
        }
    }

    public void resetNotification() {
        double progress = calculateOverallProgress();
        if (progress < NOTIFICATION_THRESHOLD * 100) {
            isNotificationSent = false;
        }
    }

    public void checkAllGoalsProgress() {
        for (Category category : categories) {
            category.checkAllGoalsProgress();
        }
    }

    public void loadData() {
        fileManager.init(this);
    }

    public void saveData() {
        fileManager.saveAll(this);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public void displayAllGoals() {
        System.out.println("\nВсе цели в копилке:");

        for (Category category : categories) {
            System.out.println("\nКатегория: " + category.getName());

            for (Goal goal : category.getGoals()) {
                System.out.println(goal);
            }
        }
    }

    public double calculateOverallProgress() {
        double totalBalance = 0;
        double totalTarget = 0;

        for (Category category : categories) {
            for (Goal goal : category.getGoals()) {
                totalBalance += goal.getBalance();
                totalTarget += goal.getTarget();
            }
        }

        if (totalTarget == 0) return 0;
        return (totalBalance / totalTarget) * 100;
    }

    public String getFormattedOverallProgress() {
        return String.format("Общий прогресс: %.2f%%", calculateOverallProgress());
    }

    public List<Category> getAllCategories() {
        return categories;
    }
}
