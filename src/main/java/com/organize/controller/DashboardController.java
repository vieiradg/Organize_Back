package com.organize.controller;

import com.organize.dto.AppointmentDTO;
import com.organize.dto.DashboardDTO;
import com.organize.dto.FinanceDashboardDTO;
import com.organize.dto.TopCustomerDTO;
import com.organize.service.DashboardService;
import com.organize.service.FinanceDashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final FinanceDashboardService financeDashboardService;

    public DashboardController(DashboardService dashboardService,
                               FinanceDashboardService financeDashboardService) {
        this.dashboardService = dashboardService;
        this.financeDashboardService = financeDashboardService;
    }
    @GetMapping("/common")
    public DashboardDTO getCommonDashboard(@RequestHeader("establishmentId") UUID establishmentId) {
        return dashboardService.getCommonDashboard(establishmentId);
    }

    @GetMapping("/common/upcoming-appointments")
    public List<AppointmentDTO> getUpcomingAppointments(@RequestHeader("establishmentId") UUID establishmentId) {
        return dashboardService.getUpcomingAppointments(establishmentId);
    }

    @GetMapping("/common/top-customers")
    public List<TopCustomerDTO> getTopCustomers(@RequestHeader("establishmentId") UUID establishmentId) {
        return dashboardService.getTopCustomers(establishmentId);
    }


    @GetMapping("/finance")
    public FinanceDashboardDTO getFinanceDashboard(@RequestHeader("adminId") UUID adminId) {
        return financeDashboardService.getFinanceDashboard(adminId);
    }
}
