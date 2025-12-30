package org.example.service;

import lombok.NonNull;
import org.example.dao.UserDao;
import org.example.model.User;
import org.example.proxy.LogUserDaoProxy;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = new LogUserDaoProxy(userDao);
    }

    @Override
    public User createUser(@NonNull User user) {
        return userDao.save(user);
    }

    @Override
    public Optional<User> getUserById(@NonNull Long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(@NonNull String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(@NonNull User user) {
        return userDao.update(user);
    }

    @Override
    public void deleteUser(@NonNull Long id) {
        userDao.delete(id);
    }
}
