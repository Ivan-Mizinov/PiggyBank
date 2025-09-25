import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private String description;
    private List<Goal> goals = new ArrayList<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void checkAllGoalsProgress() {
        for (Goal goal : goals) {
            goal.checkAndNotifyProgress();
        }
    }

    public String getDescription() {
        return description;
    }

    public List<Goal> getGoals() {
        return goals;
    }
    public String getName() {
        return name;
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public void removeGoal(Goal goal) {
        goals.remove(goal);
    }

    @Override
    public String toString() {
        return "Категория: " + name +
                "\nОписание: " + description +
                "\nКоличество целей: " + goals.size();
    }
}
