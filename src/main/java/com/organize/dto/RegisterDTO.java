package com.organize.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
    @NotBlank
    String name,
    
    @NotBlank
    String email,
    
    String phone,
    
    @NotBlank
    String password
    
) {
}