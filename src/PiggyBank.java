import java.util.ArrayList;
import java.util.List;

public class PiggyBank {
    private List<Category> categories = new ArrayList<>();

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public void displayAllGoals() {
        System.out.println("Все цели в копилке:");

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
