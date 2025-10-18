package com.organize.dto;

import java.util.UUID;

public record TopCustomerDTO(
        UUID clientId,
    String name,
    long revenue,
    long appointmentCount
) {}
