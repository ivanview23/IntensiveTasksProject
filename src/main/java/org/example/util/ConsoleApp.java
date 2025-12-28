package org.example.util;

import org.example.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);

    public static void displayMenu() {
        System.out.println("\n### Выберите необходимый пункт меню! ###");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по Id");
        System.out.println("3. Найти пользователя по почте");
        System.out.println("4. Показать всех пользователей");
        System.out.println("5. Обновить пользователя");
        System.out.println("6. Удалить пользователя");
        System.out.println("7. Выход");
    }

    public static User readUserInput() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите почту: ");
        String email = scanner.nextLine();

        System.out.print("Укажите возраст: ");
        Integer age = Integer.parseInt(scanner.nextLine());

        return User.builder()
                .name(name)
                .email(email)
                .age(age)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Long readIdInput() {
        System.out.print("Введите id пользователя: ");
        return Long.parseLong(scanner.nextLine());
    }

    public static String readEmailInput() {
        System.out.print("Введите почту пользователя: ");
        return scanner.nextLine();
    }

    public static User readUserInputForUpdate() {
        System.out.print("Введите id пользователя для обновления: ");
        long id = Long.parseLong(scanner.nextLine());

        User user = readUserInput();
        user.setId(id);
        return user;
    }

    public static void displayUser(User user) {
        if (user != null) {
            System.out.println("\nПользователь id: " + user.getId());
            System.out.println("Имя: " + user.getName());
            System.out.println("Почта: " + user.getEmail());
            System.out.println("Возраст: " + user.getAge());
            System.out.println("Дата создания: " + user.getCreatedAt());
        } else {
            System.out.println("Пользователь не найден!");
        }
    }

    public static void displayUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены!");
        } else {
            System.out.println("\nСписок всех пользователей: ");
            users.forEach(ConsoleApp::displayUser);
        }
    }
}
