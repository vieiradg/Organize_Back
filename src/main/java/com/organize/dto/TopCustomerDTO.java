package com.organize.dto;

public record TopCustomerDTO(
    String name,
    long revenue,
    long appointmentCount
) {}
