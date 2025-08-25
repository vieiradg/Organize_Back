package com.organize.dto;

import com.organize.model.User;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String email,
        String phone) {
    public UserDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone());
    }
}