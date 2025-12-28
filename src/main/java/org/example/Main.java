package org.example;

import org.example.config.HibernateConfig;
import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.example.util.ConsoleApp;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            UserDao userDao = new UserDaoImpl();
            UserService userService = new UserServiceImpl(userDao);

            boolean running = true;

            while (running) {
                try {
                    ConsoleApp.displayMenu();
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch (choice) {
                        case 1 -> {
                            User user = ConsoleApp.readUserInput();
                            User createdUser = userService.createUser(user);
                            System.out.println("Пользователь успешно создан!");
                            ConsoleApp.displayUser(createdUser);
                        }
                        case 2 -> {
                            long id = ConsoleApp.readIdInput();
                            Optional<User> user = userService.getUserById(id);
                            user.ifPresentOrElse(
                                    ConsoleApp::displayUser,
                                    () -> System.out.println("Пользователь не найден!")
                            );
                        }
                        case 3 -> {
                            String email = ConsoleApp.readEmailInput();
                            Optional<User> user = userService.getUserByEmail(email);
                            user.ifPresentOrElse(
                                    ConsoleApp::displayUser,
                                    () -> System.out.println("Пользователь не найден!")
                            );
                        }
                        case 4 -> {
                            List<User> users = userService.getAllUsers();
                            ConsoleApp.displayUsers(users);
                        }
                        case 5 -> {
                            User user = ConsoleApp.readUserInputForUpdate();
                            User updatedUser = userService.updateUser(user);
                            System.out.println("Пользователь успешно обновлен!");
                            ConsoleApp.displayUser(updatedUser);
                        }
                        case 6 -> {
                            long id = ConsoleApp.readIdInput();
                            userService.deleteUser(id);
                            System.out.println("Пользователь успешно удален!");
                        }
                        case 7 -> {
                            running = false;
                            System.out.println("Exiting...");
                        }
                        default -> System.out.println("Нет такого пункта меню!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Пожалуйста введите число!");
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка ввода: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateConfig.shutdown();
        }
    }
}
