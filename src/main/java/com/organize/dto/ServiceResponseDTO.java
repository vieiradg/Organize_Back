package com.organize.dto;

import com.organize.model.BeautyService;
import java.math.BigDecimal;

public record ServiceResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer duration
) {
    public ServiceResponseDTO(BeautyService service) {
        this(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDuration()
        );
    }
}