package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.NotificationRequestDto;
import org.example.dto.NotificationResponseDto;
import org.example.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "API для отправки уведомлений")
public class NotificationController {

    private final EmailService emailService;

    @Operation(summary = "Отправить уведомление")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Уведомление отправлено",
                    content = @Content(schema = @Schema(implementation = NotificationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public NotificationResponseDto sendNotification(@RequestBody NotificationRequestDto request) {
        try {
            emailService.sendEmail(request.getEmail(), request.getSubject(), request.getMessage());

            return NotificationResponseDto.builder()
                    .status("SUCCESS")
                    .message("Уведомление успешно отправлено")
                    .build();
        } catch (Exception e) {
            return NotificationResponseDto.builder()
                    .status("ERROR")
                    .message("Ошибка при отправке уведомления: " + e.getMessage())
                    .build();
        }
    }
}