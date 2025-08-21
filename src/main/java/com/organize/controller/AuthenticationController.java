package com.organize.controller;

import com.organize.dto.AuthenticationDTO;
import com.organize.dto.LoginResponseDTO;
import com.organize.dto.PasswordResetConfirmDTO;
import com.organize.dto.PasswordResetRequestDTO;
import com.organize.dto.RegisterDTO;
import com.organize.model.User;
import com.organize.repository.UserRepository;
import com.organize.security.TokenService;
import com.organize.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AuthService authService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository, TokenService tokenService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        if (this.userRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(
                data.name(),
                data.email(),
                encryptedPassword,
                data.phone(),
                data.roles()
        );
        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid PasswordResetRequestDTO requestDTO) {
        authService.createPasswordResetTokenForUser(requestDTO.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetConfirmDTO confirmDTO) {
        authService.resetPassword(confirmDTO.token(), confirmDTO.newPassword());
        return ResponseEntity.ok().build();
    }
}
