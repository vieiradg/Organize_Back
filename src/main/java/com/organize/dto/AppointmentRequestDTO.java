package com.organize.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequestDTO(
        UUID clientId,
        UUID serviceId,
        UUID employeeId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {}
