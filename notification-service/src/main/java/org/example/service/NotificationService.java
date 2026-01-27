package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(String eventJson) {
        log.info("Получено событие из Kafka: {}", eventJson);

        try {
            UserEventDto event = objectMapper.readValue(eventJson, UserEventDto.class);


        String email = event.getEmail();
        String subject = "Уведомление от сайта";
        String message;

        switch (event.getEventType()) {
            case CREATED:
                message = String.format(
                        "Здравствуйте, %s! Ваш аккаунт на сайте был успешно создан.",
                        event.getUserName() != null ? event.getUserName() : "уважаемый пользователь"
                );
                break;
            case DELETED:
                message = "Здравствуйте! Ваш аккаунт был удалён.";
                break;
            default:
                log.warn("Неизвестный тип события: {}", event.getEventType());
                return;
        }


            emailService.sendEmail(email, subject, message);
            log.info("Уведомление отправлено на email: {}", email);
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления на email", e);
        }
    }
}