package com.organize.controller;

import com.organize.dto.DashboardDTO;
import com.organize.dto.FinanceDashboardDTO;
import com.organize.service.DashboardService;
import com.organize.service.FinanceDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final FinanceDashboardService financeDashboardService;

    public DashboardController(DashboardService dashboardService, FinanceDashboardService financeDashboardService) {
        this.dashboardService = dashboardService;
        this.financeDashboardService = financeDashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboardData(
        @RequestHeader("adminId") UUID adminId
    ) {
        return ResponseEntity.ok(dashboardService.getDashboardData(adminId));
    }

    @GetMapping("/finance")
    public ResponseEntity<FinanceDashboardDTO> getFinanceDashboard(
        @RequestHeader("adminId") UUID adminId
    ) {
        return ResponseEntity.ok(financeDashboardService.getFinanceDashboard(adminId));
    }
}
