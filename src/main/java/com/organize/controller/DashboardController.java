package com.organize.controller;

import com.organize.dto.DashboardDTO;
import com.organize.model.User;
import com.organize.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard(@AuthenticationPrincipal User loggedUser) {
        DashboardDTO dashboardData = dashboardService.getDashboardData(loggedUser.getId());
        return ResponseEntity.ok(dashboardData);
    }
}
