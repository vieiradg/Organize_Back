package com.organize.dto;

import com.organize.model.Establishment;
import com.organize.model.Role;
import com.organize.model.User;

import java.util.UUID;
import java.util.stream.Collectors;

public record LoginResponseDTO(String token, UserInfoDTO user, UUID establishmentId) {
    
    public LoginResponseDTO(String token, User user, Establishment establishment) {
        this(
            token, 
            new UserInfoDTO(
                user.getId(), 
                user.getName(),
                user.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.toSet())
            ), 
            (establishment != null ? establishment.getId() : null)
        );
    }
}