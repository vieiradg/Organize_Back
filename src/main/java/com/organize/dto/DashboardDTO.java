package com.organize.dto;

import java.util.List;

public record DashboardDTO(
        long monthlyRevenue,
        double revenueGrowthPercentage,

        long appointmentsToday,
        long confirmedAppointmentsToday,

        String nextAppointmentTime,
        String nextAppointmentDescription,

        long newCustomers,
        double newCustomersGrowthPercentage,

        List<AppointmentDTO> upcomingAppointments,
        List<TopCustomerDTO> topCustomers
) {}
