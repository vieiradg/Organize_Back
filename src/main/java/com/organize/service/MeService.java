package com.organize.service;

import com.organize.dto.ChangePasswordRequestDTO;
import com.organize.dto.ProfileResponseDTO;
import com.organize.dto.UpdateProfileRequestDTO;
import java.util.UUID;

public interface MeService {
    ProfileResponseDTO getUserProfile(UUID userId);
    ProfileResponseDTO updateUserProfile(UUID userId, UpdateProfileRequestDTO dto);
    void changeUserPassword(UUID userId, ChangePasswordRequestDTO dto);
}