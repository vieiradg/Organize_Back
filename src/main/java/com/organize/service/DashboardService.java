package com.organize.service;

import com.organize.dto.AppointmentDTO;
import com.organize.dto.DashboardDTO;
import com.organize.model.Appointment;
import com.organize.model.AppointmentStatus;
import com.organize.model.Establishment;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final TransactionsRepository transactionsRepository;
    private final EstablishmentRepository establishmentRepository;

    public DashboardService(
            AppointmentRepository appointmentRepository,
            TransactionsRepository transactionsRepository,
            EstablishmentRepository establishmentRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.transactionsRepository = transactionsRepository;
        this.establishmentRepository = establishmentRepository;
    }

    private Establishment getAdminEstablishment(UUID adminId) {
        return establishmentRepository.findByOwnerId(adminId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado: " + adminId));
    }

    public DashboardDTO getDashboardData(UUID adminId) {

        Establishment est = getAdminEstablishment(adminId);
        UUID establishmentId = est.getId();

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);

        long monthlyRevenue = transactionsRepository.sumRevenueByEstablishmentAndDateRange(
                establishmentId,
                startOfMonth.toLocalDate(),
                endOfMonth.toLocalDate()
        );

        List<Appointment> todayAppointments =
                appointmentRepository.findAppointmentsByEstablishmentAndDateRange(
                        establishmentId,
                        startOfDay,
                        endOfDay
                );

        long appointmentsToday = todayAppointments.size();

        long confirmedAppointmentsToday = todayAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED)
                .count();

        long newCustomers = appointmentRepository.countNewCustomers(establishmentId, startOfMonth);

        List<Appointment> futureAppointments =
                appointmentRepository.findAllByEstablishmentIdAndStartTimeAfter(
                        establishmentId, now
                );

        List<AppointmentDTO> upcomingAppointments =
                futureAppointments.stream()
                        .sorted(Comparator.comparing(Appointment::getStartTime))
                        .map(AppointmentDTO::new)
                        .toList();

        AppointmentDTO nextAppointment = upcomingAppointments.isEmpty()
                ? null
                : upcomingAppointments.get(0);

        return new DashboardDTO(
                monthlyRevenue,
                appointmentsToday,
                confirmedAppointmentsToday,
                newCustomers,
                upcomingAppointments,
                nextAppointment
        );
    }
}
