package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .build();
    }

    public UserResponseDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .age(entity.getAge())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntity(User user, UserRequestDto dto) {
        if (dto == null) return;

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
    }
}