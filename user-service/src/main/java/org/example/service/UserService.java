package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserEventDto;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private static final String USER_EVENTS_TOPIC = "user-events";

    public UserResponseDto createUser(UserRequestDto userDto) {
        log.info("Создание пользователя: {}", userDto);

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        User user = userMapper.toEntity(userDto);
        user.setCreatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);

        sendUserEvent(UserEventDto.EventType.CREATED, savedUser);

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

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));

        sendUserEvent(UserEventDto.EventType.DELETED, user);

        userRepository.deleteById(id);
    }

    private void sendUserEvent(UserEventDto.EventType eventType, User user) {
        try {
            UserEventDto event = UserEventDto.builder()
                    .eventType(eventType)
                    .email(user.getEmail())
                    .userId(user.getId())
                    .userName(user.getName())
                    .build();

            String eventJson = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(USER_EVENTS_TOPIC, eventJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Событие отправлено в Kafka: {} для пользователя {}", eventType, user.getEmail());
                        } else {
                            log.error("Ошибка отправки события в Kafka: {}", ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Ошибка при отправке события в Kafka", e);
        }
    }
}