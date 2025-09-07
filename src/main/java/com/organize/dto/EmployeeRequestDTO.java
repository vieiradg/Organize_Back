package com.organize.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmployeeRequestDTO(
        @NotNull
        UUID userId,
        @NotNull
        UUID establishmentId,
        @NotBlank
        String name,
        String role
) {
}
