package com.organize.dto;

import com.organize.model.TransactionStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        UUID appointment_id,
        UUID establishment_id,
        String description,
        int amount_cents,
        LocalDate transaction_date,
        TransactionStatus status,
        String clientName 
) {
}