package com.organize.dto;

import com.organize.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponseDTO(
    UUID id,
    String name,
    String email,
    String phone,
    LocalDateTime lastVisit,
    Integer appointmentsCount
) {
    public CustomerResponseDTO(User user, LocalDateTime lastVisit, Integer appointmentsCount) {
        this(user.getId(), user.getName(), user.getEmail(), user.getPhone(), lastVisit, appointmentsCount);
    }
}