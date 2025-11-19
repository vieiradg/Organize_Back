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

    @Query("SELECT a FROM Appointment a WHERE a.client.id = :clientId AND a.startTime BETWEEN :start AND :end")
    List<Appointment> findAppointmentsByClientAndDateRange(@Param("clientId") UUID clientId,
                                                           @Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);

    List<Appointment> findByClient(User client);

    List<Appointment> findAllByEstablishmentIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(
            UUID establishmentId,
            LocalDateTime startTime,
            AppointmentStatus status
    );



    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.employee.id = :employeeId
        AND ((a.startTime < :endTime) AND (a.endTime > :startTime))
    """)
    boolean isEmployeeUnavailable(@Param("employeeId") UUID employeeId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    @Query("""
        SELECT a FROM Appointment a
        JOIN FETCH a.client c
        JOIN FETCH a.offeredService s
        JOIN FETCH a.employee e
        WHERE a.establishment.id = :establishmentId
        AND a.startTime BETWEEN :start AND :end
        ORDER BY a.startTime ASC
    """)
    List<Appointment> findAppointmentsByEstablishmentAndDateRange(
            @Param("establishmentId") UUID establishmentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Appointment> findAllByEstablishmentIdAndStartTimeAfter(
            UUID establishmentId,
            LocalDateTime startTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT a.client_id)
        FROM appointments a
        WHERE a.establishment_id = :establishmentId
        AND DATE_TRUNC('month', a.start_time) = DATE_TRUNC('month', CAST(:startOfMonth AS timestamp))
        AND a.client_id NOT IN (
            SELECT ap.client_id
            FROM appointments ap
            WHERE ap.establishment_id = :establishmentId
            AND ap.start_time < CAST(:startOfMonth AS timestamp)
        )
    """, nativeQuery = true)
    Long countNewCustomers(
            @Param("establishmentId") UUID establishmentId,
            @Param("startOfMonth") LocalDateTime startOfMonth
    );
}