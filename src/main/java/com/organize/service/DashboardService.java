package com.organize.service;

import com.organize.dto.DashboardDTO;
import com.organize.dto.AppointmentDTO;
import com.organize.dto.TopCustomerDTO;
import com.organize.dto.RecentReviewDTO;
import com.organize.model.Appointment;
import com.organize.model.AppointmentStatus;
import com.organize.model.User;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.UserRepository;
import com.organize.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import com.organize.dto.RecentReviewDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public DashboardService(AppointmentRepository appointmentRepository,
                            UserRepository userRepository,
                            ReviewRepository reviewRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    public DashboardDTO getDashboardData() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimHoje = hoje.atTime(23, 59, 59);

        // 1. Faturamento do mês
        double monthlyRevenue = appointmentRepository.findByStartTimeBetween(
                hoje.withDayOfMonth(1).atStartOfDay(),
                hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(23, 59, 59)
        ).stream()
         .mapToDouble(a -> a.getService().getPriceCents() / 100.0)
         .sum();

        // 2. Agendamentos de hoje
        long appointmentsToday = appointmentRepository.countByStartTimeBetween(inicioHoje, fimHoje);

        // 3. Agendamentos confirmados de hoje
        long confirmedAppointmentsToday = appointmentRepository.countByStartTimeBetweenAndStatus(
                inicioHoje, fimHoje, AppointmentStatus.CONFIRMED
        );

        // 4. Próximo agendamento
        Appointment nextAppointment = appointmentRepository.findFirstByStartTimeAfterOrderByStartTimeAsc(LocalDateTime.now())
                .orElse(null);
        String nextAppointmentTime = nextAppointment != null ? nextAppointment.getStartTime().toString() : null;
        String nextAppointmentDescription = nextAppointment != null
                ? nextAppointment.getClient().getName() + " - " + nextAppointment.getService().getName()
                : null;

        // 5. Novos clientes no mês
        long newCustomers = userRepository.countByCreatedAtAfter(
                hoje.withDayOfMonth(1).atStartOfDay()
        );

        // 6. Próximos agendamentos
        List<AppointmentDTO> upcomingAppointments = appointmentRepository.findByStartTimeAfter(LocalDateTime.now()).stream()
                .sorted(Comparator.comparing(Appointment::getStartTime))
                .limit(5)
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        // 7. Principais clientes
        List<TopCustomerDTO> topCustomers = appointmentRepository.findByStartTimeBetween(
                hoje.withDayOfMonth(1).atStartOfDay(),
                hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(23, 59, 59)
        ).stream()
        .collect(Collectors.groupingBy(Appointment::getClient,
                Collectors.summingDouble(a -> a.getService().getPriceCents() / 100.0)))
        .entrySet().stream()
        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
        .limit(3)
        .map(entry -> new TopCustomerDTO(
                entry.getKey().getName(),
                Math.round(entry.getValue()),
                (int) appointmentRepository.findByClient(entry.getKey()).size()
        ))
        .collect(Collectors.toList());

        // 8. Avaliações recentes
        List<RecentReviewDTO> recentReviews = reviewRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(r -> new RecentReviewDTO(
                        r.getClient().getName(),
                        r.getRating(),
                        r.getComment()
                ))
                .collect(Collectors.toList());

        return new DashboardDTO(
                monthlyRevenue,
                appointmentsToday,
                confirmedAppointmentsToday,
                nextAppointmentTime,
                nextAppointmentDescription,
                newCustomers,
                upcomingAppointments,
                topCustomers,
                recentReviews
        );
    }
}
