package com.organize.service;

import com.organize.dto.*;
import com.organize.model.Appointment;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.ClientDataRepository;
import com.organize.repository.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TransactionsRepository transactionsRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClientDataRepository clientDataRepository;

    public DashboardService(
            TransactionsRepository transactionsRepository,
            AppointmentRepository appointmentRepository,
            ClientDataRepository clientDataRepository
    ) {
        this.transactionsRepository = transactionsRepository;
        this.appointmentRepository = appointmentRepository;
        this.clientDataRepository = clientDataRepository;
    }

    public DashboardDTO getCommonDashboard(UUID establishmentId) {
        return new DashboardDTO(
                calculateCurrentRevenue(establishmentId),
                calculateRevenueGrowth(establishmentId),
                calculateAppointmentsToday(establishmentId),
                calculateConfirmedAppointmentsToday(establishmentId),
                getNextAppointmentTime(establishmentId),
                getNextAppointmentDescription(establishmentId),
                calculateNewCustomersCurrent(establishmentId),
                calculateNewCustomerGrowth(establishmentId),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public List<AppointmentDTO> getUpcomingAppointments(UUID establishmentId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endMonth = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX);

        return appointmentRepository.findAppointmentsByEstablishmentAndDateRange(establishmentId, now, endMonth)
                .stream()
                .sorted(Comparator.comparing(Appointment::getStartTime))
                .limit(5)
                .map(AppointmentDTO::new)
                .toList();
    }

    public List<TopCustomerDTO> getTopCustomers(UUID establishmentId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        Map<UUID, Long> appointmentCountByClient = appointmentRepository
                .findAppointmentsByEstablishmentAndDateRange(establishmentId, start, end)
                .stream()
                .collect(Collectors.groupingBy(a -> a.getClient().getId(), Collectors.counting()));

        return appointmentCountByClient.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    UUID clientId = entry.getKey();
                    long appointmentCount = entry.getValue();

                    var clientData = clientDataRepository.findByClientId(clientId);
                    String name = clientData.map(cd -> cd.getClient().getName()).orElse("Desconhecido");

                    long revenue = appointmentRepository
                            .findAppointmentsByClientAndDateRange(clientId, start, end)
                            .stream()
                            .mapToLong(a -> transactionsRepository.findByAppointmentId(a.getId())
                                    .stream()
                                    .mapToLong(t -> t.getAmountCents())
                                    .sum())
                            .sum();

                    return new TopCustomerDTO(clientId, name, revenue, appointmentCount);
                })
                .toList();
    }

    private long calculateCurrentRevenue(UUID establishmentId) {
        YearMonth month = YearMonth.now();
        return transactionsRepository.sumRevenueByEstablishmentAndDateRange(establishmentId, month.atDay(1), month.atEndOfMonth());
    }

    private double calculateRevenueGrowth(UUID establishmentId) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth lastMonth = currentMonth.minusMonths(1);
        long current = transactionsRepository.sumRevenueByEstablishmentAndDateRange(establishmentId, currentMonth.atDay(1), currentMonth.atEndOfMonth());
        long previous = transactionsRepository.sumRevenueByEstablishmentAndDateRange(establishmentId, lastMonth.atDay(1), lastMonth.atEndOfMonth());
        return calculatePercentageGrowth(current, previous);
    }

    private long calculateAppointmentsToday(UUID establishmentId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return appointmentRepository.findAppointmentsByEstablishmentAndDateRange(establishmentId, start, end).size();
    }

    private long calculateConfirmedAppointmentsToday(UUID establishmentId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return appointmentRepository.findAppointmentsByEstablishmentAndDateRange(establishmentId, start, end)
                .stream()
                .filter(a -> a.getStatus().name().equals("CONFIRMED"))
                .count();
    }

    private String getNextAppointmentTime(UUID establishmentId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endMonth = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX);
        return appointmentRepository.findAppointmentsByEstablishmentAndDateRange(establishmentId, now, endMonth)
                .stream()
                .filter(a -> a.getStartTime().isAfter(now))
                .min(Comparator.comparing(Appointment::getStartTime))
                .map(a -> a.getStartTime().toString())
                .orElse(null);
    }

    private String getNextAppointmentDescription(UUID establishmentId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endMonth = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX);
        return appointmentRepository.findAppointmentsByEstablishmentAndDateRange(establishmentId, now, endMonth)
                .stream()
                .filter(a -> a.getStartTime().isAfter(now))
                .min(Comparator.comparing(Appointment::getStartTime))
                .map(a -> a.getService().getName())
                .orElse(null);
    }

    private long calculateNewCustomersCurrent(UUID establishmentId) {
        YearMonth currentMonth = YearMonth.now();
        return clientDataRepository.findByEstablishmentId(establishmentId).stream()
                .filter(c -> YearMonth.from(c.getCreatedAt()).equals(currentMonth))
                .count();
    }

    private double calculateNewCustomerGrowth(UUID establishmentId) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth lastMonth = currentMonth.minusMonths(1);
        long current = clientDataRepository.findByEstablishmentId(establishmentId).stream()
                .filter(c -> YearMonth.from(c.getCreatedAt()).equals(currentMonth))
                .count();
        long previous = clientDataRepository.findByEstablishmentId(establishmentId).stream()
                .filter(c -> YearMonth.from(c.getCreatedAt()).equals(lastMonth))
                .count();
        return calculatePercentageGrowth(current, previous);
    }

    private double calculatePercentageGrowth(double current, double previous) {
        if (previous <= 0) return current > 0 ? 100.0 : 0.0;
        return ((current - previous) / previous) * 100.0;
    }
}
