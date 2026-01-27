package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDto {
    private EventType eventType;
    private String email;
    private Long userId;
    private String userName;

    public enum EventType {
        CREATED,
        DELETED
    }
}
