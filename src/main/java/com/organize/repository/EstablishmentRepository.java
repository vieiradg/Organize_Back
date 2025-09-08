package com.organize.repository;

import com.organize.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 
import java.util.UUID;

public interface EstablishmentRepository extends JpaRepository<Establishment, UUID> {

    Optional<Establishment> findByOwnerId(UUID ownerId);
}