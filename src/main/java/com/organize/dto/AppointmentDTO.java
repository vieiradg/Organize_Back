package com.organize.dto;

import com.organize.model.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentDTO(
        UUID id,
        UUID clientId,
        String clientName,
        UUID serviceId,
        String serviceName,
        UUID employeeId,
        String employeeName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {
    public AppointmentDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getClient().getId(),
                appointment.getClient().getName(),
                appointment.getService().getId(),
                appointment.getService().getName(),
                appointment.getEmployee().getId(),
                appointment.getEmployee().getName(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus().name()
        );
    }
}
