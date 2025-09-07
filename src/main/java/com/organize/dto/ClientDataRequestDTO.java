package com.organize.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClientDataRequestDTO(
        @NotNull(message = "ID do cliente é obrigatório") UUID clientId,

        String privateNotes) {
}