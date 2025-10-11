package com.organize.service;

import com.organize.dto.ChangePasswordRequestDTO;
import com.organize.dto.ProfileResponseDTO;
import com.organize.dto.UpdateProfileRequestDTO;
import com.organize.model.User;
import com.organize.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeServiceImpl implements MeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDTO getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return new ProfileResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhone());
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateUserProfile(UUID userId, UpdateProfileRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        
        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            if (userRepository.existsByEmailAndIdNot(dto.email(), userId)) {
                throw new IllegalArgumentException("O e-mail informado já está em uso.");
            }
            user.setEmail(dto.email());
        }

        User updatedUser = userRepository.save(user);
        return new ProfileResponseDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getPhone());
    }

    @Override
    @Transactional
    public void changeUserPassword(UUID userId, ChangePasswordRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha antiga incorreta.");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }
}