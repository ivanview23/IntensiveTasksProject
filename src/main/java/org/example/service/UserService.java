package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto createUser(UserRequestDto userDto) {
        log.info("Создание пользователя: {}", userDto);

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("Поиск пользователя по id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        log.info("Поиск пользователя по почте: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + email + " не найден"));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.info("Поиск всех пользователей");

        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(Long id, UserRequestDto userDto) {
        log.info("Обновление пользователя с id {}: {}", id, userDto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        if (userDto.getEmail() != null &&
                !userDto.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        userMapper.updateEntity(user, userDto);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Удаление пользователя по id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }

        userRepository.deleteById(id);
    }
}