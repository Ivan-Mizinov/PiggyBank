import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileManager {
    private static final String CATEGORIES_FILE = "categories.txt";
    private static final String GOALS_FILE = "goals.txt";
    private static final String DEPOSITS_FILE = "deposits.txt";

    // Сохранение категорий в файл
    public void saveCategories(List<Category> categories) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATEGORIES_FILE))) {
            for (Category category : categories) {
                writer.write(category.getName() + "|" + category.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения категорий: " + e.getMessage());
        }
    }

    // Загрузка категорий из файла
    public List<Category> loadCategories() {
        List<Category> categories = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CATEGORIES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    categories.add(new Category(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки категорий: " + e.getMessage());
        }
        return categories;
    }

    // Сохранение целей в файл
    public void saveGoals(List<Goal> goals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GOALS_FILE))) {
            for (Goal goal : goals) {
                writer.write(goal.getName() + "|" +
                        goal.getCategory().getName() + "|" +
                        goal.getTarget() + "|" +
                        goal.getBalance() + "|" +
                        goal.getEndDate().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения целей: " + e.getMessage());
        }
    }

    // Загрузка целей из файла
    public List<Goal> loadGoals(List<Category> categories) {
        List<Goal> goals = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(GOALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    categories.stream()
                            .filter(c -> c.getName().equals(parts[1]))
                            .findFirst().ifPresent(category -> goals.add(new Goal(
                                    parts[0],
                                    category,
                                    Double.parseDouble(parts[2]),
                                    Double.parseDouble(parts[3]),
                                    LocalDate.parse(parts[4])
                            )));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки целей: " + e.getMessage());
        }
        return goals;
    }

    public void saveDeposits(List<Goal> goals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DEPOSITS_FILE))) {
            for (Goal goal : goals) {
                for (Deposit deposit : goal.getDeposits()) {
                    writer.write(goal.getName() + "|" +
                            goal.getCategory().getName() + "|" +
                            deposit.getAmount() + "|" +
                            deposit.getDate().toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения депозитов: " + e.getMessage());
        }
    }

    public void loadDeposits(List<Goal> goals) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DEPOSITS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String goalName = parts[0];
                    String categoryName = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3]);

                    Optional<Goal> goal = goals.stream()
                            .filter(g -> g.getName().equals(goalName)
                                    && g.getCategory().getName().equals(categoryName))
                            .findFirst();

                    goal.ifPresent(g -> g.addDeposit(amount, date));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки депозитов: " + e.getMessage());
        }
    }

    // Метод для инициализации приложения данными из файлов
    public void init(PiggyBank piggyBank) {
        List<Category> categories = loadCategories();
        piggyBank.getAllCategories().addAll(categories);

        List<Goal> goals = loadGoals(categories);
        categories.forEach(category -> category.getGoals().clear());
        goals.forEach(goal -> goal.getCategory().addGoal(goal));

        loadDeposits(goals);
    }

    // Метод для сохранения всех данных
    public void saveAll(PiggyBank piggyBank) {
        List<Category> categories = piggyBank.getAllCategories();
        List<Goal> allGoals = categories.stream()
                .flatMap(c -> c.getGoals().stream())
                .collect(Collectors.toList());
        saveCategories(categories);
        saveGoals(allGoals);

        saveDeposits(allGoals);
    }
}
