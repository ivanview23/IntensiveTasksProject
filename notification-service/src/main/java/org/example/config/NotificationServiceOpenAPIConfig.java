package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class NotificationServiceOpenAPIConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .description("API для отправки уведомлений")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Server"),
                        new Server()
                                .url("http://api.example.com")
                                .description("Production Server")
                ));
    }
}
