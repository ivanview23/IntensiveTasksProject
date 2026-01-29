package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на отправку уведомления")
public class NotificationRequestDto {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Email получателя", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Тема не может быть пустой")
    @Schema(description = "Тема письма", example = "Важное уведомление", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;

    @NotBlank(message = "Сообщение не может быть пустым")
    @Schema(description = "Текст сообщения", example = "Здравствуйте! Это тестовое уведомление.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
