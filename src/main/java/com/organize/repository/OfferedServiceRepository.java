package com.organize.repository;

import com.organize.model.OfferedService;
import com.organize.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OfferedServiceRepository extends JpaRepository<OfferedService, UUID> {
    @Query("SELECT s FROM OfferedService s WHERE s.establishment.owner = :owner")
    List<OfferedService> findServicesByOwner(@Param("owner") User owner);

    List<OfferedService> findByEstablishmentId(UUID establishmentId);
}
