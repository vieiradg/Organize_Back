package com.organize.dto;

import com.organize.model.TransactionStatus;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionDTO(
        UUID appointment_id,
        UUID establishment_id,
        String description,
        int amount_cents,
        LocalDate transaction_date,
        TransactionStatus status
) {

}
