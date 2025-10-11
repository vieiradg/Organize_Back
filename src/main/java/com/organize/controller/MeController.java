package com.organize.controller;

import com.organize.dto.ChangePasswordRequestDTO;
import com.organize.dto.ProfileResponseDTO;
import com.organize.dto.UpdateProfileRequestDTO;
import com.organize.model.User; 
import com.organize.service.MeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final MeService meService;

    @GetMapping
    public ResponseEntity<ProfileResponseDTO> getMyProfile(@AuthenticationPrincipal User currentUser) {
        ProfileResponseDTO profile = meService.getUserProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<ProfileResponseDTO> updateMyProfile(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody UpdateProfileRequestDTO dto) {
        ProfileResponseDTO updatedProfile = meService.updateUserProfile(currentUser.getId(), dto);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changeMyPassword(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody ChangePasswordRequestDTO dto) {
        meService.changeUserPassword(currentUser.getId(), dto);
        return ResponseEntity.noContent().build();
    }
}