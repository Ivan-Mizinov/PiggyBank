void main() {
    ConsoleUI console = new ConsoleUI();

    System.out.println("-----------------------------------------");
    System.out.println("Добро пожаловать в приложение 'Копилка'!");
    System.out.println("-----------------------------------------");

    try {
        console.start();
    } finally {
        console.close();
        System.out.println("Спасибо за использование приложения!");
    }
}