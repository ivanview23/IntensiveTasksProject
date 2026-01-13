package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setName("Иван Иванов");
        request.setEmail("ivan@mail.ru");
        request.setAge(30);

        UserResponseDto response = UserResponseDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        given(userService.createUser(any(UserRequestDto.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void getUserById() throws Exception {
        UserResponseDto response = UserResponseDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        given(userService.getUserById(1L)).willReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван Иванов"));
    }

    @Test
    void getAllUsers() throws Exception {
        UserResponseDto user1 = UserResponseDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(30)
                .build();

        UserResponseDto user2 = UserResponseDto.builder()
                .id(2L)
                .name("Иван Иванов")
                .email("ivan@mail.ru")
                .age(25)
                .build();

        List<UserResponseDto> users = Arrays.asList(user1, user2);

        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Иван Иванов"))
                .andExpect(jsonPath("$[1].name").value("Иван Иванов"));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}