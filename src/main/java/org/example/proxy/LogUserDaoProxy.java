package org.example.proxy;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
public class LogUserDaoProxy implements UserDao {

    private final UserDao realUserDao;

    public LogUserDaoProxy(UserDao realUserDao) {
        this.realUserDao = realUserDao;
    }

    @Override
    public User save(User user) {
        log.info("Создание пользователя: {}", user);
        return realUserDao.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        log.info("Поиск пользователя по id: {} ", id);
        return realUserDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Поиск пользователя по почте: {} ", email);
        return realUserDao.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        log.info("Поиск всех пользователей");
        return realUserDao.findAll();
    }

    @Override
    public User update(User user) {
        log.info("Обновление пользователя: {}", user);
        return realUserDao.update(user);
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление пользователя по id: {} ", id);
        realUserDao.delete(id);
    }
}
