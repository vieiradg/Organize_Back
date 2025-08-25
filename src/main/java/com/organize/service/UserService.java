package com.organize.service;

import com.organize.dto.UserDTO;
import com.organize.model.User;
import com.organize.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // READ (UM) - Pelo Email (usado no login)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // READ (UM) - Pelo ID
    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    // READ (TODOS)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::new) 
                .collect(Collectors.toList());
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    // UPDATE
    public User updateUser(UUID id, UserDTO userDTO) {
        User user = findUserById(id); 

        user.setName(userDTO.name());
        user.setPhone(userDTO.phone());

        return userRepository.save(user);
    }

    // DELETE (Inativação)
    public void inactivateUser(UUID id, User authenticatedUser) {
        if (!authenticatedUser.getId().equals(id)) {
            throw new SecurityException("Você só pode inativar sua própria conta.");
        }

        User user = findUserById(id); 
        user.setActive(false);
        userRepository.save(user);
    }
}