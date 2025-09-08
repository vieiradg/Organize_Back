package com.organize.dto;

import java.util.List;

public record DashboardDTO(
    long monthlyRevenue,
    long appointmentsToday,
    long confirmedAppointmentsToday,
    String nextAppointmentTime,
    String nextAppointmentDescription,
    long newCustomers,
    List<AppointmentDTO> upcomingAppointments,
    List<TopCustomerDTO> topCustomers,
    List<RecentReviewDTO> recentReviews
) {}
