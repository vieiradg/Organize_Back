package com.organize.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ServiceRequestDTO(
        @NotBlank
        String name,
        String description,
        @NotNull
        BigDecimal price,
        @NotNull
        Integer duration
) {
}
