package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.example.dto.UserEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        partitions = 1,
        topics = {"user-events"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@ActiveProfiles("test")
@DirtiesContext
class KafkaEmailIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private org.example.service.EmailService emailService;

    @BeforeEach
    void setUp() {
        reset(emailService);
    }

    @Test
    void whenUserCreatedEventSent_thenEmailShouldBeSent() throws Exception {
        UserEventDto event = UserEventDto.builder()
                .eventType("CREATED")
                .email("test@example.com")
                .userId(1L)
                .userName("Test User")
                .build();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("user-events", event.getEmail(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("Failed to send message: " + ex.getMessage());
                ex.printStackTrace();
            } else {
                System.out.println("Message sent successfully: " + result.getRecordMetadata());
            }
        });

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(emailService, atLeastOnce())
                            .sendEmail(eq("test@example.com"), anyString(), anyString());
                });

        verify(emailService).sendEmail(
                eq("test@example.com"),
                eq("Уведомление от сервиса"),
                contains("создан")
        );
    }

    @Test
    void whenUserDeletedEventSent_thenEmailShouldBeSent() {
        UserEventDto event = UserEventDto.builder()
                .eventType("DELETED")
                .email("delete-test@example.com")
                .userId(2L)
                .userName("Deleted User")
                .build();

        kafkaTemplate.send("user-events", event.getEmail(), event);

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(emailService, atLeastOnce())
                            .sendEmail(eq("delete-test@example.com"), anyString(), anyString());
                });

        verify(emailService).sendEmail(
                eq("delete-test@example.com"),
                eq("Уведомление от сервиса"),
                contains("удалён")
        );
    }

    @Test
    void whenInvalidEventSent_thenEmailShouldNotBeSent() {
        UserEventDto event = UserEventDto.builder()
                .eventType("INVALID_TYPE")
                .email("invalid@example.com")
                .userId(3L)
                .userName("Invalid User")
                .build();

        kafkaTemplate.send("user-events", event.getEmail(), event);

        Awaitility.await()
                .during(3, TimeUnit.SECONDS)
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(emailService, never())
                            .sendEmail(anyString(), anyString(), anyString());
                });
    }
}