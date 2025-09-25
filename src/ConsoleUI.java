import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            System.out.println("\nМеню Копилки:");
            System.out.println("1. Создать новую цель");
            System.out.println("2. Просмотреть все цели");
            System.out.println("3. Показать общий прогресс");
            System.out.println("4. Обновить баланс цели");
            System.out.println("0. Сохранить и выйти");

            int choice = getIntInput("Выберите пункт меню: ");

            switch (choice) {
                case 1:
                    createGoal();
                    break;
                case 2:
                    piggyBank.displayAllGoals();
                    break;
                case 3:
                    System.out.println(piggyBank.getFormattedOverallProgress());
                    break;
                case 4:
                    updateGoalBalance();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
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
}