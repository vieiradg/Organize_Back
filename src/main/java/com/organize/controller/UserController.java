package com.organize.controller;

import com.organize.dto.UserDTO;
import com.organize.model.User;
import com.organize.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // READ (Listar todos) - Protegido por Role na SecurityConfigurations
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // READ (Dados do usuário autenticado)
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getAuthenticatedUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserDTO(user));
    }

    // READ (Buscar por ID)
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(new UserDTO(user));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal User authenticatedUser) {
        if (!authenticatedUser.getId().equals(id)) {
            return ResponseEntity.status(403).build(); 
        }

        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }

    // DELETE (Inativar)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivateUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal User authenticatedUser) {
        userService.inactivateUser(id, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}