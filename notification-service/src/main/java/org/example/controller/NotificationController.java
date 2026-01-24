package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.NotificationRequestDto;
import org.example.dto.NotificationResponseDto;
import org.example.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public NotificationResponseDto sendNotification(@RequestBody NotificationRequestDto request) {
        try {
            emailService.sendEmail(request.getEmail(), request.getUser(), request.getMessage());

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