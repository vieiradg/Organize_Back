package com.organize.service;

import com.organize.dto.FinanceDashboardDTO;
import com.organize.model.Establishment;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class FinanceDashboardService {

    private final TransactionsRepository transactionsRepository;
    private final AppointmentRepository  appointmentRepository;
    private final EstablishmentRepository establishmentRepository;

    public FinanceDashboardService(TransactionsRepository transactionsRepository,
                                   EstablishmentRepository establishmentRepository,
                                   AppointmentRepository appointmentRepository) {
        this.transactionsRepository = transactionsRepository;
        this.establishmentRepository = establishmentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    private Establishment getAdminEstablishment(UUID adminId) {
        return establishmentRepository.findByOwnerId(adminId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado para admin: " + adminId));
    }

    public FinanceDashboardDTO getFinanceDashboard(UUID adminId) {
        Establishment est = getAdminEstablishment(adminId);
        UUID establishmentId = est.getId();

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        LocalDate prevStart = previousMonth.atDay(1);
        LocalDate prevEnd = previousMonth.atEndOfMonth();

        long revenue = transactionsRepository.sumRevenueByEstablishmentAndDateRange(establishmentId, start, end);
        long expenses = transactionsRepository.sumExpensesByEstablishmentAndDateRange(establishmentId, start, end);
        long profit = revenue + expenses;

        long prevRevenue = transactionsRepository.sumRevenueByEstablishmentAndDateRange(establishmentId, prevStart, prevEnd);
        long prevExpenses = transactionsRepository.sumExpensesByEstablishmentAndDateRange(establishmentId, prevStart, prevEnd);
        long prevProfit = prevRevenue + prevExpenses;

        double revenueGrowth = prevRevenue > 0 ? ((double) (revenue - prevRevenue) / prevRevenue) * 100 : 0;
        double profitGrowth = prevProfit > 0 ? ((double) (profit - prevProfit) / prevProfit) * 100 : 0;

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        int totalAppointments = appointmentRepository
                .findAppointmentsByEstablishmentAndDateRange(establishmentId, startDateTime, endDateTime)
                .size();

        double avgRevenuePerAppointment = totalAppointments > 0 ? (double) revenue / totalAppointments : 0;

        return new FinanceDashboardDTO(
                revenue,
                expenses,
                profit,
                revenueGrowth,
                profitGrowth,
                avgRevenuePerAppointment,
                totalAppointments
        );
    }
}
