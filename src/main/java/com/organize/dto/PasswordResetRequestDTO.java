package com.organize.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDTO(
        @NotBlank
        @Email
        String email
) {
}
