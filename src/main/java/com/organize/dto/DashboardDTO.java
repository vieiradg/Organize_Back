package com.organize.dto;

import java.util.List;

public record DashboardDTO(
        long monthlyRevenue,
        long appointmentsToday,
        long confirmedAppointmentsToday,
        long newCustomers,
        List<AppointmentDTO> upcomingAppointments,
        AppointmentDTO nextAppointment
) {}
