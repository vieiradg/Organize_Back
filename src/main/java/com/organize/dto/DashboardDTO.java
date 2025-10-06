package com.organize.dto;

import java.util.List;

public record DashboardDTO(
    double monthlyRevenue,   // alterado de long para double
    long appointmentsToday,
    long confirmedAppointmentsToday,
    String nextAppointmentTime,
    String nextAppointmentDescription,
    long newCustomers,
    List<AppointmentDTO> upcomingAppointments,
    List<TopCustomerDTO> topCustomers,
    List<RecentReviewDTO> recentReviews
) {}
