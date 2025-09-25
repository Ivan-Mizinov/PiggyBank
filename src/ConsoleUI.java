import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private PiggyBank piggyBank;
    private Scanner scanner;

    public ConsoleUI() {
        this.piggyBank = new PiggyBank();
        this.scanner = new Scanner(System.in);

        piggyBank.loadData();
    }

    public void start() {
        boolean running = true;
        while (running) {
            checkAndShowReminders();

            System.out.println("\nМеню Копилки:");
            System.out.println("1. Создать новую цель");
            System.out.println("2. Удалить цель");
            System.out.println("3. Просмотреть все цели");
            System.out.println("4. Показать общий прогресс");
            System.out.println("5. Обновить баланс цели");
            System.out.println("6. Добавить новую категорию");
            System.out.println("7. Удалить категорию");
            System.out.println("8. Показать напоминания");
            System.out.println("0. Сохранить и выйти");

            piggyBank.checkAllGoalsProgress();
            piggyBank.checkAndNotifyProgress();

            int choice = getIntInput("Выберите пункт меню: ");

            switch (choice) {
                case 1:
                    createGoal();
                    break;
                case 2:
                    removeGoal();
                    break;
                case 3:
                    piggyBank.displayAllGoals();
                    break;
                case 4:
                    System.out.println(piggyBank.getFormattedOverallProgress());
                    break;
                case 5:
                    updateGoalBalance();
                    break;
                case 6:
                    addCategory();
                    break;
                case 7:
                    removeCategory();
                    break;
                case 8:
                    showReminders();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private void showReminders() {
        List<Goal> allGoals = getAllGoals();
        boolean anyReminder = false;

        for (Goal goal : allGoals) {
            LocalDate today = LocalDate.now();
            if (goal.getEndDate().isAfter(today)) {
                long daysLeft = ChronoUnit.DAYS.between(today, goal.getEndDate());
                System.out.println(
                        "Цель: " + goal.getName() +
                                ", До завершения: " + daysLeft + " дней"
                );
                anyReminder = true;
            }
        }
        if (!anyReminder) {
            System.out.println("Активных напоминаний нет");
        }
    }

    private void removeGoal() {
        List<Goal> allGoals = getAllGoals();

        if (allGoals.isEmpty()) {
            System.out.println("Нет целей для удаления!");
            return;
        }

        System.out.println("\nДоступные цели для удаления:");
        for (int i = 0; i < allGoals.size(); i++) {
            System.out.println((i + 1) + ". " + allGoals.get(i).getName());
        }

        int choice = getIntInput("Выберите цель для удаления: ");

        if (choice > 0 && choice <= allGoals.size()) {
            Goal selectedGoal = allGoals.get(choice - 1);
            Category category = selectedGoal.getCategory();

            try {
                removeGoalFromCategory(category, selectedGoal);
                System.out.println("Цель '" + selectedGoal.getName() + "' успешно удалена!");
            } catch (Exception e) {
                System.out.println("Ошибка при удалении цели!");
            }
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    private void removeGoalFromCategory(Category category, Goal goal) {
        if (category != null) {
            category.removeGoal(goal);
        } else {
            throw new RuntimeException("Категория не найдена");
        }
    }

    private void addCategory() {
        String name = getStringInput("Введите название новой категории: ");
        if (piggyBank.categoryExists(name)) {
            System.out.println("Категория с таким именем уже существует!");
            return;
        }
        String description = getStringInput("Введите описание для новой категории: ");
        Category newCategory = new Category(name, description);
        piggyBank.addCategory(newCategory);
    }

    private void removeCategory() {
        List<Category> emptyCategories = getEmptyCategories();

        if (emptyCategories.isEmpty()) {
            System.out.println("Нет категорий для удаления (все категории содержат цели)");
            return;
        }

        System.out.println("\nДоступные категории для удаления (пустые):");
        for (int i = 0; i < emptyCategories.size(); i++) {
            System.out.println((i + 1) + ". " + emptyCategories.get(i).getName());
        }

        int choice = getIntInput("Выберите категорию для удаления: ");

        if (choice > 0 && choice <= emptyCategories.size()) {
            String categoryName = emptyCategories.get(choice - 1).getName();
            piggyBank.removeCategory(categoryName);
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    private List<Category> getEmptyCategories() {
        List<Category> emptyCategories = new ArrayList<>();
        for (Category category : piggyBank.getAllCategories()) {
            if (category.getGoals().isEmpty()) {
                emptyCategories.add(category);
            }
        }
        return emptyCategories;
    }

    private void updateGoalBalance() {
        if (piggyBank.getAllCategories().isEmpty()) {
            System.out.println("Нет доступных целей для обновления!");
            return;
        }

        System.out.println("\nВыберите цель для обновления баланса:");
        List<Goal> allGoals = getAllGoals();

        for (int i = 0; i < allGoals.size(); i++) {
            System.out.println((i + 1) + ". " + allGoals.get(i).getName() +
                    " (Текущий баланс: " + allGoals.get(i).getBalance() + "), (Нужная сумма: " + allGoals.get(i).getTarget() +")");
        }

        int choice = getIntInput("Выберите цель: ");

        if (choice > 0 && choice <= allGoals.size()) {
            Goal selectedGoal = allGoals.get(choice - 1);
            double amount = getDoubleInput("Введите сумму изменения баланса (положительную или отрицательную): ");
            double newBalance = selectedGoal.getBalance() + amount;

            if (newBalance < 0) {
                System.out.println("Ошибка! Нельзя установить отрицательный баланс!");
                System.out.println("Текущий баланс: " + selectedGoal.getBalance());
                System.out.println("Предлагаемое изменение: " + amount);
                return;
            }

            if (newBalance > selectedGoal.getTarget()) {
                System.out.println("Предупреждение! Новый баланс превышает целевую сумму!");
                System.out.println("Текущая цель: " + selectedGoal.getTarget());
                System.out.println("Предполагаемый баланс: " + newBalance);

                if (!confirmAction()) {
                    return;
                }
            }

            selectedGoal.setBalance(newBalance);
            System.out.println("Баланс успешно обновлен!");

            piggyBank.checkAllGoalsProgress();
            piggyBank.checkAndNotifyProgress();
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    private List<Goal> getAllGoals() {
        List<Goal> allGoals = new ArrayList<>();
        for (Category category : piggyBank.getAllCategories()) {
            allGoals.addAll(category.getGoals());
        }
        return allGoals;
    }

    private boolean confirmAction() {
        System.out.println("Хотите продолжить? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y");
    }

    private Category selectOrCreateCategory() {
        System.out.println("\nДоступные категории:");
        List<Category> categories = piggyBank.getAllCategories();

        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName());
        }

        System.out.println("0. Создать новую категорию");
        int choice = getIntInput("Выберите категорию или создайте новую (0): ");

        if (choice == 0) {
            String categoryName = getStringInput("Введите название новой категории: ");
            String categoryDesc = getStringInput("Введите описание категории: ");

            Category newCategory = new Category(categoryName, categoryDesc);
            piggyBank.addCategory(newCategory);
            return newCategory;
        } else if (choice > 0 && choice <= categories.size()) {
            return categories.get(choice - 1);
        } else {
            System.out.println("Неверный выбор категории!");
            return selectOrCreateCategory();
        }
    }

    private void createGoal() {
        String name = getStringInput("Введите название цели: ");
        Category category = selectOrCreateCategory();
        double target = getDoubleInput("Введите целевую сумму: ");
        double balance = getDoubleInput("Введите текущую сумму: ");
        LocalDate endDate = getDateInput();
        new Goal(name, category, target, balance, endDate);
        System.out.println("Цель успешно создана!");
        piggyBank.resetNotification();
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите целое число!");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число!");
            }
        }
    }

    private LocalDate getDateInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                System.out.print("Введите дату завершения (yyyy-MM-dd): ");
                return LocalDate.parse(scanner.nextLine(), formatter);
            } catch (Exception e) {
                System.out.println("Неверный формат даты! Используйте yyyy-MM-dd");
            }
        }
    }

    public void close() {
        piggyBank.saveData();

        if (scanner != null) {
            scanner.close();
        }
    }

    private void checkAndShowReminders() {
        List<Goal> allGoals = getAllGoals();
        boolean anyReminderShown = false;

        for (Goal goal : allGoals) {
            if (goal.needsReminder()) {
                LocalDate today = LocalDate.now();
                long daysLeft = ChronoUnit.DAYS.between(today, goal.getEndDate());
                System.out.println(
                        "⚠️⚠️⚠️ Напоминание: до завершения цели '" +
                                goal.getName() + "' осталось " + daysLeft + " дней! ⚠️⚠️⚠️"
                );
                anyReminderShown = true;
            }
        }

        if (!anyReminderShown) {
            System.out.println("Напоминаний нет");
        }
    }
}