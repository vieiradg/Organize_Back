package com.organize.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AppointmentRequestDTO(
        UUID customerId,
        UUID serviceId,
        UUID establishmentId,
        UUID employeeId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        String clientNotes
) {}
