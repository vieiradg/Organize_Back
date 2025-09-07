package com.organize.dto;

import com.organize.model.OfferedService;

import java.util.UUID;

public record OfferedServiceResponseDTO(
        UUID id,
        String name,
        String description,
        Integer priceCents,
        Integer duration
) {
    public OfferedServiceResponseDTO(OfferedService service) {
        this(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPriceCents(),
                service.getDurationMinutes()
        );
    }
}
