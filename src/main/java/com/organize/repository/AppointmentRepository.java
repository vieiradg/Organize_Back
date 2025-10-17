package com.organize.repository;

import com.organize.model.Appointment;
import com.organize.model.User;
import com.organize.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Consultas existentes
    @Query("SELECT a FROM Appointment a WHERE a.client.id = :clientId AND a.startTime BETWEEN :start AND :end")
    List<Appointment> findAppointmentsByClientAndDateRange(@Param("clientId") UUID clientId,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);

    List<Appointment> findByClient(User client);

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.employee.id = :employeeId
        AND ((a.startTime < :endTime) AND (a.endTime > :startTime))
    """)
    boolean isEmployeeUnavailable(@Param("employeeId") UUID employeeId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    @Query("SELECT a FROM Appointment a WHERE a.establishment.id = :establishmentId AND a.startTime BETWEEN :start AND :end")
    List<Appointment> findAppointmentsByEstablishmentAndDateRange(@Param("establishmentId") UUID establishmentId,
                                                                  @Param("start") LocalDateTime start,
                                                                  @Param("end") LocalDateTime end);

    // ===========================
    // Novos métodos para Dashboard
    // ===========================

    // Próximo agendamento futuro confirmado do cliente
    Optional<Appointment> findTopByClientIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(
            UUID clientId,
            LocalDateTime now,
            AppointmentStatus status
    );

    // Total de agendamentos do cliente
    long countByClientId(UUID clientId);

    // Lista de próximos agendamentos (ordenados por data)
    List<Appointment> findByClientIdAndStartTimeAfterOrderByStartTimeAsc(UUID clientId, LocalDateTime now);
}
