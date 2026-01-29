package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ с данными пользователя")
public class UserResponseDto {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "18")
    private Integer age;

    @Schema(description = "Дата создания", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;


}