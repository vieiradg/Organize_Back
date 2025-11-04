package com.organize.controller;

import com.organize.service.GeminiReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class GeminiReportController {

    private final GeminiReportService geminiReportService;

    public GeminiReportController(GeminiReportService geminiReportService) {
        this.geminiReportService = geminiReportService;
    }

    @PostMapping("/monthly")
    public ResponseEntity<?> generateMonthlyReport(@RequestParam UUID adminId) {
        try {
            String report = geminiReportService.generateMonthlyReport(adminId);
            return ResponseEntity.status(HttpStatus.CREATED).body(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> listReports(@RequestParam UUID adminId) {
        try {
            return ResponseEntity.ok(geminiReportService.listReports(adminId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Erro ao listar relatórios: " + e.getMessage());
        }
    }

    @GetMapping("/monthly/{reportId}")
    public ResponseEntity<?> getReportDetails(@PathVariable UUID reportId) {
        try {
            return ResponseEntity.ok(geminiReportService.getReportDetails(reportId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("📄 Relatório não encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Erro ao buscar relatório detalhado: " + e.getMessage());
        }
    }

}
