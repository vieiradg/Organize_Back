package com.organize.repository;

import com.organize.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstablishmentRepository extends JpaRepository<Establishment, UUID> {
}
