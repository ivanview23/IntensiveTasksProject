package org.example.dao;

import org.example.config.TestHibernateConfig;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private UserDao userDao;
    private SessionFactory testSessionFactory;

    @BeforeAll
    void setupAll() {
        System.out.println("JDBC URL: " + postgresContainer.getJdbcUrl());
        System.out.println("Username: " + postgresContainer.getUsername());
        System.out.println("Password: " + postgresContainer.getPassword());

        testSessionFactory = TestHibernateConfig.getSessionFactory();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(testSessionFactory);
        cleanDatabase();
    }

    private void cleanDatabase() {
        try (Session session = testSessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            session.createNativeQuery("DELETE FROM users").executeUpdate();

            session.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();

            tx.commit();
        } catch (Exception e) {
            System.err.println("Ошибка очистки БД: " + e.getMessage());
        }
    }

    @AfterAll
    void tearDownAll() {
        TestHibernateConfig.shutdown();
    }

    @Test
    @DisplayName("Должен сохранить и найти пользователя в реальной БД")
    void saveAndFindUser_IntegrationTest() {
        User user = User.builder()
                .name("Интеграционный Тест")
                .email("integration@test.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userDao.save(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("Интеграционный Тест", savedUser.getName());

        Optional<User> foundUser = userDao.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getName(), foundUser.get().getName());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Должен найти пользователя по email")
    void findByEmail_ShouldReturnUser() {
        String email = "findbyemail@test.com";
        User user = User.builder()
                .name("Поиск по Email")
                .email(email)
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user);

        Optional<User> result = userDao.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске несуществующего email")
    void findByEmail_ShouldReturnEmpty_WhenEmailNotFound() {
        Optional<User> result = userDao.findByEmail("nonexistent@test.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Должен вернуть всех пользователей")
    void findAll_ShouldReturnAllUsers() {
        User user1 = User.builder()
                .name("User 1")
                .email("user1@test.com")
                .age(20)
                .createdAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .name("User 2")
                .email("user2@test.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user1);
        userDao.save(user2);

        List<User> users = userDao.findAll();

        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void update_ShouldUpdateUser() {
        User user = User.builder()
                .name("Original")
                .email("update@test.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userDao.save(user);

        savedUser.setName("Updated");
        savedUser.setAge(35);

        User updatedUser = userDao.update(savedUser);

        assertEquals("Updated", updatedUser.getName());
        assertEquals(35, updatedUser.getAge());
        assertEquals("update@test.com", updatedUser.getEmail());

        Optional<User> fromDb = userDao.findById(savedUser.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Updated", fromDb.get().getName());
    }

    @Test
    @DisplayName("Должен удалить пользователя")
    void delete_ShouldRemoveUser() {
        User user = User.builder()
                .name("To Delete")
                .email("delete@test.com")
                .age(40)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userDao.save(user);
        Long userId = savedUser.getId();

        assertTrue(userDao.findById(userId).isPresent());

        userDao.delete(userId);

        Optional<User> deletedUser = userDao.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Должен бросить исключение при дубликате email")
    void save_ShouldThrowException_WhenDuplicateEmail() {
        String email = "duplicate@test.com";

        User user1 = User.builder()
                .name("First")
                .email(email)
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .name("Second")
                .email(email)
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();

        userDao.save(user1);

        assertThrows(Exception.class, () -> {
            userDao.save(user2);
        });
    }

    @Test
    @DisplayName("Не должен бросать исключение при удалении несуществующего ID")
    void delete_ShouldNotThrow_WhenUserDoesNotExist() {
        assertDoesNotThrow(() -> {
            userDao.delete(999999L);
        });
    }
}