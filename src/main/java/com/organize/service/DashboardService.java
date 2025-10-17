package com.organize.service;
import com.organize.dto.AppointmentDTO;
import com.organize.dto.DashboardDTO;
import com.organize.model.Appointment;
import com.organize.model.AppointmentStatus;
import com.organize.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final AppointmentRepository appointmentRepository;

    public DashboardService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public DashboardDTO getDashboardData(UUID clientId) {
        LocalDateTime now = LocalDateTime.now();

        // Buscar o próximo agendamento confirmado
        Appointment nextAppointment = appointmentRepository
                .findTopByClientIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(
                        clientId,
                        now,
                        AppointmentStatus.CONFIRMED
                )
                .orElse(null);

        // Total de agendamentos do cliente
        long totalAppointments = appointmentRepository.countByClientId(clientId);

        // Lista de agendamentos futuros
        List<AppointmentDTO> upcomingAppointments = appointmentRepository
                .findByClientIdAndStartTimeAfterOrderByStartTimeAsc(clientId, now)
                .stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        AppointmentDTO nextDTO = nextAppointment != null ? new AppointmentDTO(nextAppointment) : null;

        // Retorna o DTO preenchendo apenas os campos relevantes
        return new DashboardDTO(
                0,                    // monthlyRevenue
                0,                    // appointmentsToday
                0,                    // confirmedAppointmentsToday
                nextDTO != null ? nextDTO.startTime().toString() : null, // nextAppointmentTime
                nextDTO != null ? nextDTO.serviceName() : null,          // nextAppointmentDescription
                0,                    // newCustomers
                upcomingAppointments, // upcomingAppointments
                Collections.emptyList(), // topCustomers
                Collections.emptyList(), // recentReviews
                nextDTO,              // nextAppointment
                totalAppointments     // totalAppointments
        );
    }
