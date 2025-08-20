package com.organize.dto;

import com.organize.model.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentDTO(
        UUID id,
        UUID clientId,
        String clientName,
        String serviceName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {
    public AppointmentDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getClient().getId(),
                appointment.getClient().getName(),
                appointment.getService().getName(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus().name()  
        );
    }
}
