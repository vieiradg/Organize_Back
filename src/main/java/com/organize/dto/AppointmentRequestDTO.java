package com.organize.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record AppointmentRequestDTO(
        Long customerId,
        Set<Long> serviceIds,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {
}
