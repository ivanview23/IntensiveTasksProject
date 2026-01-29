package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ на запрос отправки уведомления")
public class NotificationResponseDto {

    @Schema(description = "Статус отправки", example = "SUCCESS")
    private String status;

    @Schema(description = "Сообщение о результате", example = "Уведомление успешно отправлено")
    private String message;
}