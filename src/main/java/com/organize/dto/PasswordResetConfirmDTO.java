package com.organize.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmDTO(
        @NotBlank
        String token,
        @NotBlank
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String newPassword
) {
}
