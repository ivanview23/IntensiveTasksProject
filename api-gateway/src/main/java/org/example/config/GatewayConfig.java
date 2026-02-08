package org.example.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .build())
                .build());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .prefixPath("/api/v1")
                                .stripPrefix(2)
                                .addRequestHeader("X-Gateway", "user-service")
                                .circuitBreaker(config -> config
                                        .setName("userServiceCB")
                                        .setFallbackUri("forward:/fallback/user-service")
                                ))
                        .uri("lb://USER-SERVICE"))

                .route("notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f
                                .prefixPath("/api/v1")
                                .stripPrefix(2)
                                .addRequestHeader("X-Gateway", "notification-service")
                                .circuitBreaker(config -> config
                                        .setName("notificationServiceCB")
                                        .setFallbackUri("forward:/fallback/notification-service")
                                ))
                        .uri("lb://NOTIFICATION-SERVICE"))

                .route("gateway-health", r -> r
                        .path("/health")
                        .filters(f -> f.setPath("/actuator/health"))
                        .uri("http://localhost:8080"))

                .route("default-circuit-breaker", r -> r
                        .path("/api/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("defaultCB")
                                        .setFallbackUri("forward:/fallback/default")
                                ))
                        .uri("lb://UNKNOWN-SERVICE"))
                .build();
    }
}