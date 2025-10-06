package com.organize.controller;

import com.organize.dto.DashboardDTO;
import com.organize.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboardData() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
}
