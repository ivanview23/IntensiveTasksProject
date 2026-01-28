package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание/обновление пользователя")
public class UserRequestDto {

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    @Schema(description = "Имя пользователя", example = "Иван Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Email пользователя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull(message = "Возраст не может быть null")
    @Positive(message = "Возраст должен быть положительным числом")
    @Schema(description = "Возраст пользователя", example = "18", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer age;
}