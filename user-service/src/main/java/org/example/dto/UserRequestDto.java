package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotNull(message = "Возраст не может быть null")
    @Positive(message = "Возраст должен быть положительным числом")
    private Integer age;
}