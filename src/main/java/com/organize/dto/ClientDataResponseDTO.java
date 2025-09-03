package com.organize.dto;

import com.organize.model.ClientData;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientDataResponseDTO(
        UUID id,
        UUID clientId,
        String clientName,
        String clientEmail,
        String clientPhone,
        UUID establishmentId,
        String establishmentName,
        String privateNotes,
        Integer missedAppointmentsCount,
        LocalDateTime createdAt) {
    public ClientDataResponseDTO(ClientData clientData) {
        this(
                clientData.getId(),
                clientData.getClient().getId(),
                clientData.getClient().getName(),
                clientData.getClient().getEmail(),
                clientData.getClient().getPhone(),
                clientData.getEstablishment().getId(),
                clientData.getEstablishment().getName(),
                clientData.getPrivateNotes(),
                clientData.getMissedAppointmentsCount(),
                clientData.getCreatedAt());
    }
}