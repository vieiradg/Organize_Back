package com.organize.dto;

import com.organize.model.Establishment;
import com.organize.model.User;
import java.util.UUID;

public record LoginResponseDTO(String token, UserInfoDTO user, UUID establishmentId) {
    
    public LoginResponseDTO(String token, User user, Establishment establishment) {
        this(
            token, 
            new UserInfoDTO(user.getId(), user.getName()), 
            (establishment != null ? establishment.getId() : null)
        );
    }

    public record UserInfoDTO(UUID id, String name) {}
}