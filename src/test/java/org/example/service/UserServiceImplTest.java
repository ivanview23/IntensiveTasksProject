package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao);
    }

    private User createTestUser(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createUserTest() {
        User createUser = createTestUser(1L, "Иван Иванов", "ivan@mail.ru");

        when(userDao.save(any(User.class))).thenReturn(createUser);
        User result = userService.createUser(createUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван Иванов", result.getName());

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void getUserByIdShouldReturnUser() {
        User createdUser = createTestUser(2L, "Иван Иванов", "ivan@mail.ru");
        when(userDao.findById(2L)).thenReturn(Optional.of(createdUser));

        Optional<User> result = userService.getUserById(2L);

        assertTrue(result.isPresent());
        assertEquals(createdUser, result.get());

        verify(userDao, times(1)).findById(2L);
    }

    @Test
    void getUserByIdShouldReturnEmpty() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());

        verify(userDao, times(1)).findById(999L);
    }

    @Test
    void getUserByEmailShouldReturnUser() {
        String email = "ivan@mail.ru";
        User createdUser = createTestUser(1L, "Иван Иванов", "ivan@mail.ru");
        when(userDao.findByEmail(email)).thenReturn(Optional.of(createdUser));

        Optional<User> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userDao, times(1)).findByEmail(email);
    }

    @Test
    void getAllUsersShouldReturnAllUsers() {
        User createdUser = createTestUser(1L, "Иван Иванов", "ivan@mail.ru");
        List<User> users = List.of(createdUser);
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(createdUser, result.get(0));
        verify(userDao, times(1)).findAll();
    }

    @Test
    void updateUserShouldUpdateAndReturnUser() {
        User updatedUser = User.builder()
                .id(1L)
                .name("Обновленный Иван")
                .email("ivan.new@mail.com")
                .age(31)
                .createdAt(LocalDateTime.now())
                .build();

        when(userDao.update(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(updatedUser);

        assertEquals("Обновленный Иван", result.getName());
        assertEquals("ivan.new@mail.com", result.getEmail());
        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    void deleteUserShouldCallDeleteMethod() {
        doNothing().when(userDao).delete(1L);

        userService.deleteUser(1L);

        verify(userDao, times(1)).delete(1L);
    }

//    Негативные тесты

    @Test
    void createUserShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            userService.createUser(null);
        });

        verify(userDao, never()).save(any());
    }

    @Test
    void getUserByIdShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            userService.getUserById(null);
        });

        verify(userDao, never()).findById(any());
    }

    @Test
    void getUserByEmailShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            userService.getUserByEmail(null);
        });

        verify(userDao, never()).findByEmail(any());
    }

    @Test
    void deleteUserShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            userService.deleteUser(null);
        });

        verify(userDao, never()).delete(any());
    }

    @Test
    void updateUserShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            userService.updateUser(null);
        });

        verify(userDao, never()).update(any());
    }
}