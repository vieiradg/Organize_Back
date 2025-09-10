package com.organize.dto;

import java.util.UUID;

public record EstablishmentDTO(
    UUID id,
    String name,
    String address,
    String contactPhone
) {}