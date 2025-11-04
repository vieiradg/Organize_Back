package com.organize.repository;

import com.organize.model.GeminiReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeminiReportRepository extends JpaRepository<GeminiReport, UUID> {
    Optional<GeminiReport> findByAdminIdAndReportMonth(UUID adminId, LocalDate month);
    List<GeminiReport> findByAdminIdOrderByReportMonthDesc(UUID adminId);
}
