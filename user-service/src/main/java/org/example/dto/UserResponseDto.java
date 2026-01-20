package org.example.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;
}