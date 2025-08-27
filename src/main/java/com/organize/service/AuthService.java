package com.organize.service;

import com.organize.model.PasswordResetToken;
import com.organize.model.User;
import com.organize.repository.PasswordResetTokenRepository;
import com.organize.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService { 

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);

        System.out.println("Token de redefinição de senha para " + email + ": " + token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));

        if (passToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado.");
        }

        User user = passToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passToken);
    }
}