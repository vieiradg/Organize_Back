package com.organize.repository;

import com.organize.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
