package com.organize.repository;

import com.organize.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
List<Appointment> findByClientIdAndStartTimeBetween(UUID clientId, LocalDateTime start, LocalDateTime end);
}
