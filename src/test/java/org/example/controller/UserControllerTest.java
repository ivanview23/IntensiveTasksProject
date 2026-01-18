package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"))
                .andExpect(jsonPath("$.age").value(30));

        assertThat(userRepository.count()).isEqualTo(1);
        User savedUser = userRepository.findAll().get(0);
        assertThat(savedUser.getName()).isEqualTo("Иван Иванов");
        assertThat(savedUser.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void createUserWhenInvalidData() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .name("")
                .email("invalid-email")
                .age(-5)
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.age").exists());
    }

    @Test
    void getUserById() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .build();
        UserResponseDto savedUser = userService.createUser(request);

        mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));
    }

    @Test
    void getUserByIdWhenUserNotExists() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers() throws Exception {
        UserRequestDto user1 = UserRequestDto.builder()
                .name("Иван Иванов")
                .email("ivan1@mail.ru")
                .age(30)
                .build();
        UserRequestDto user2 = UserRequestDto.builder()
                .name("Петр Петров")
                .email("ivan2@mail.ru")
                .age(25)
                .build();

        userService.createUser(user1);
        userService.createUser(user2);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void updateUser() throws Exception {
        UserRequestDto createRequest = UserRequestDto.builder()
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .build();
        UserResponseDto savedUser = userService.createUser(createRequest);

        UserRequestDto updateRequest = UserRequestDto.builder()
                .name("Иван Обновленный")
                .email("ivan.new@mail.ru")
                .age(31)
                .build();

        mockMvc.perform(put("/api/v1/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Иван Обновленный"))
                .andExpect(jsonPath("$.email").value("ivan.new@mail.ru"))
                .andExpect(jsonPath("$.age").value(31));
    }

    @Test
    void deleteUser() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .build();
        UserResponseDto savedUser = userService.createUser(request);

        assertThat(userRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/api/v1/users/{id}", savedUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteUserWhenUserNotExists() throws Exception {
        mockMvc.perform(delete("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .build();
        userService.createUser(request);

        mockMvc.perform(get("/api/v1/users/email/ivan@mail.ru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));
    }
}