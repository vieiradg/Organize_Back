package com.organize.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(
        @NotBlank
        String name,
        String phone
) {
}
