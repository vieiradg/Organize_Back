package com.organize.repository;

import com.organize.model.User;
import com.organize.model.Role; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findByRolesContaining(Role role);

    boolean existsByEmailAndIdNot(String email, UUID id);
}