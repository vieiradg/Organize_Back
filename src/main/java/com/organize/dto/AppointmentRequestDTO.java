package com.organize.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.organize.model.AppointmentStatus;

public record AppointmentRequestDTO(
    UUID customerId,
    UUID serviceId,
    UUID employeeId,
    UUID establishmentId,
    String clientNotes,
    LocalDateTime startTime,
    LocalDateTime endTime,
    AppointmentStatus status 
) {}
