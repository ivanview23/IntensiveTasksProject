package org.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback(ServerWebExchange exchange) {
        return Mono.just(createFallbackResponse(
                "User Service is currently unavailable",
                HttpStatus.SERVICE_UNAVAILABLE,
                exchange
        ));
    }

    @GetMapping("/notification-service")
    public Mono<ResponseEntity<Map<String, Object>>> notificationServiceFallback(ServerWebExchange exchange) {
        return Mono.just(createFallbackResponse(
                "Notification Service is currently unavailable",
                HttpStatus.SERVICE_UNAVAILABLE,
                exchange
        ));
    }

    @GetMapping("/default")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback(ServerWebExchange exchange) {
        return Mono.just(createFallbackResponse(
                "Service is temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE,
                exchange
        ));
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(
            String message,
            HttpStatus status,
            ServerWebExchange exchange) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("path", exchange.getRequest().getPath().value());

        return ResponseEntity.status(status).body(response);
    }
}