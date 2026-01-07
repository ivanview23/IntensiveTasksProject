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

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao);

        testUser = User.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@mail.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createUserTest() {
        when(userDao.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);

        assertEquals(1L, createdUser.getId());

        assertEquals("Иван Иванов", createdUser.getName());

        verify(userDao, times(1)).save(any(User.class));

        System.out.println("Тест createUser_ShouldSaveUserAndReturnIt прошел успешно!");
    }

    @Test
    void getUserByIdShouldReturnUser() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());

        assertEquals(testUser, result.get());

        verify(userDao, times(1)).findById(1L);
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
        String email = "ivan@mail.com";
        when(userDao.findByEmail(email)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userDao, times(1)).findByEmail(email);
    }

    @Test
    void getAllUsersShouldReturnAllUsers() {
        List<User> users = List.of(testUser);
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
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