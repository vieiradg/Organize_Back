package com.organize.dto;

import java.time.LocalDate;
import java.util.UUID;

public record GeminiReportDetailDTO(
        UUID id,
        UUID adminId,
        LocalDate reportMonth,
        String decryptedContent,
        String createdAt
) {}
