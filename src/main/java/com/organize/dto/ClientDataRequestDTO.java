package com.organize.dto;

import jakarta.validation.constraints.NotNull;

public record ClientDataRequestDTO(
        @NotNull(message = "nome é um campo obrigatório") String name,
        @NotNull(message = "email é um campo obrigatório") String email,
        @NotNull(message = "telefone é um campo obrigatório") String phone,
        String privateNotes) {
}