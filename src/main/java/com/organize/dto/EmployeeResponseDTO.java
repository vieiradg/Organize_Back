package com.organize.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponseDTO(
        UUID id,
        UUID establishmentId,
        UUID userId,
        String name,
        String role,
        LocalDateTime createdAt
) {
}
