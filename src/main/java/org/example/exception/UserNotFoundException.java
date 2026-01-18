package org.example.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Пользователь с таким id не найден: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}