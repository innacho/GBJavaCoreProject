package ru.geekbrains.qa.java.project;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface {
    private final Controller controller = new Controller();

    public void runApplication() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Введите название города на русском языке");
            String city = scanner.nextLine();

            setGlobalCity(city);

            System.out.printf("Выберите опцию:\n 1 - Получить погоду на текущие 5 дней в городе %s," +
                    "\n 2 - Вывести результаты прошлых запросов из базы данных" +
                    "\n выход (exit) - завершить работу\n", city);

            String result = scanner.nextLine();

            checkIsExit(result);

            try {
                validateUserInput(result);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            try {
                notifyController(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void checkIsExit(String result) {
        if (result.toLowerCase().equals("выход") || result.toLowerCase().equals("exit")) {
            System.out.println("Завершаю работу");
            DatabaseRepositorySQLiteImpl myDB = ApplicationGlobalState.getInstance().getDB();
            if(myDB != null) myDB.finalizeDB();
            System.exit(0);
        }
    }

    private void setGlobalCity(String city) {
        ApplicationGlobalState.getInstance().setSelectedCity(city);
    }


    private void validateUserInput(String userInput) throws IOException {
        if (userInput == null || userInput.length() != 1) {
            throw new IOException("Incorrect user input: expected one digit as answer, but actually get " + userInput);
        }
        int answer = 0;
        try {
            answer = Integer.parseInt(userInput);
            if(answer != 1 && answer != 2) throw new IOException("Incorrect user input: unexpected number as answer: " + answer);
        } catch (NumberFormatException e) {
            throw new IOException("Incorrect user input: character is not numeric!");
        }
    }

    private void notifyController(String input) throws IOException {
        controller.onUserInput(input);
    }
}
