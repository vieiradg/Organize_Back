package com.organize.dto;

import jakarta.validation.constraints.Email;

public record UpdateProfileRequestDTO(
    String name,

    @Email(message = "Formato de email inválido se fornecido")
    String email
) {}