package com.organize.repository;

import com.organize.model.ClientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientDataRepository extends JpaRepository<ClientData, UUID> {

    List<ClientData> findByEstablishmentId(UUID establishmentId);

    Optional<ClientData> findByClientId(UUID clientId);
}
