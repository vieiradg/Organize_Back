package com.organize.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OfferedServiceRequestDTO(
        @NotBlank
        String name,
        String description,
        @NotNull
        Integer priceCents,
        @NotNull
        Integer duration,
        UUID establishmentId
) {
}
