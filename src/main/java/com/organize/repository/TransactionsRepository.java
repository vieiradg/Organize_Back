package com.organize.repository;

import com.organize.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionsRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByAppointmentId(UUID appointmentId);

    List<Transaction> findByEstablishmentIdOrderByTransactionDateDescCreatedAtDesc(UUID establishmentId);



    List<Transaction> findByEstablishmentIdAndTransactionDateBetween(UUID establishmentId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amountCents), 0) FROM Transaction t " +
            "WHERE t.establishmentId = :establishmentId " +
            "AND t.transactionDate BETWEEN :start AND :end " +
            "AND t.amountCents > 0" +
            "AND t.status = 'PAID'")
    long sumRevenueByEstablishmentAndDateRange(@Param("establishmentId") UUID establishmentId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amountCents), 0) FROM Transaction t " +
            "WHERE t.establishmentId = :establishmentId " +
            "AND t.transactionDate BETWEEN :start AND :end " +
            "AND t.amountCents < 0")
    long sumExpensesByEstablishmentAndDateRange(@Param("establishmentId") UUID establishmentId,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);

    @Query("""
    SELECT COUNT(DISTINCT t.appointmentId)
    FROM Transaction t
    WHERE t.establishmentId = :establishmentId
      AND t.status = 'PAID'
      AND t.appointmentId IS NOT NULL
      AND t.transactionDate BETWEEN :start AND :end
""")
    int countPaidAppointmentsByEstablishmentAndDateRange(
            @Param("establishmentId") UUID establishmentId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );



}
