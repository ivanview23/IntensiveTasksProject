package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserEventDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEventDto event) {
        log.info("Получено событие из Kafka: {}", event);

        String email = event.getEmail();
        String subject = "Уведомление от сайта";
        String message;

        switch (event.getEventType()) {
            case "CREATED":
                message = String.format(
                        "Здравствуйте, %s! Ваш аккаунт на сайте был успешно создан.",
                        event.getUserName() != null ? event.getUserName() : "уважаемый пользователь"
                );
                break;
            case "DELETED":
                message = "Здравствуйте! Ваш аккаунт был удалён.";
                break;
            default:
                log.warn("Неизвестный тип события: {}", event.getEventType());
                return;
        }

        try {
            emailService.sendEmail(email, subject, message);
            log.info("Уведомление отправлено на email: {}", email);
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления на email: {}", email, e);
        }
    }
}