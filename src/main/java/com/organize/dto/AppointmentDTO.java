package com.organize.dto;

import com.organize.model.Appointment;
import com.organize.model.BeautyService;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record AppointmentDTO(
        Long id,
        Long customerId,
        String customerName,
        Set<String> services,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {
    public AppointmentDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getCustomer().getId(),
                appointment.getCustomer().getName(),
                appointment.getServices().stream().map(BeautyService::getName).collect(Collectors.toSet()),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus()
        );
    }
}