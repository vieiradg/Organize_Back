package com.organize.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequestDTO(
    UUID clientId,
    UUID employeeId,
    UUID serviceId,
    UUID establishmentId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String status
) {}
