package com.organize.dto;

import com.organize.model.Customer;

public record CustomerResponseDTO(
        Long id,
        String name,
        String phone
) {
    public CustomerResponseDTO(Customer customer) {
        this(
                customer.getId(),
                customer.getName(),
                customer.getPhone()
        );
    }
}
