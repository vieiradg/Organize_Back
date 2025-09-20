package com.organize.dto;

import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateDTO(
        @NotNull String status
) {}
