package com.organize.dto;

import java.time.LocalDate;
import java.util.UUID;

public record GeminiReportResponseDTO(
        UUID id,
        UUID adminId,
        LocalDate reportMonth,
        String createdAt
) {}
