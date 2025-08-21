package com.organize.repository;

import com.organize.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    @Query("SELECT a FROM Appointment a WHERE a.client.id = :clientId AND a.startTime BETWEEN :start AND :end")
    List<Appointment> findAppointmentsByClientAndDateRange(@Param("clientId") UUID clientId,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);

}
