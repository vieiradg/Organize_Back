package com.organize.service;

import com.organize.dto.AppointmentDTO;
import com.organize.dto.DashboardDTO;
import com.organize.model.Appointment;
import com.organize.model.AppointmentStatus;
import com.organize.model.Establishment;
import com.organize.model.Role;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.TransactionsRepository; 
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final AppointmentRepository appointmentRepository;
    private final TransactionsRepository transactionsRepository; 
    private final EstablishmentRepository establishmentRepository; 

    public DashboardService(AppointmentRepository appointmentRepository,
                            TransactionsRepository transactionsRepository,
                            EstablishmentRepository establishmentRepository) { 
        this.appointmentRepository = appointmentRepository;
        this.transactionsRepository = transactionsRepository;
        this.establishmentRepository = establishmentRepository;
    }

    private Establishment getAdminEstablishment(UUID adminId) {
        return establishmentRepository.findByOwnerId(adminId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado para admin: " + adminId));
    }

    public DashboardDTO getDashboardData(UUID adminId) {
        
        Establishment est = getAdminEstablishment(adminId);
        UUID establishmentId = est.getId();

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);
        
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long monthlyRevenue = transactionsRepository
                .sumRevenueByEstablishmentAndDateRange(establishmentId, startOfMonth.toLocalDate(), endOfMonth.toLocalDate());
        
        long revenueGrowthPercent = 0; 

        List<Appointment> allAppointmentsToday = appointmentRepository.findAppointmentsByEstablishmentAndDateRange(
            establishmentId, startOfDay, endOfDay);
        
        long appointmentsToday = allAppointmentsToday.size();
        long confirmedAppointmentsToday = allAppointmentsToday.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED)
                .count();

        Appointment nextAppointment = appointmentRepository
                .findTopByClientIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(
                        adminId, 
                        now,
                        AppointmentStatus.CONFIRMED
                )
                .orElse(null);

        List<AppointmentDTO> upcomingAppointments = appointmentRepository
                .findAppointmentsByEstablishmentAndDateRange(establishmentId, now, now.plusDays(7)) 
                .stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        AppointmentDTO nextDTO = nextAppointment != null ? new AppointmentDTO(nextAppointment) : null;

        return new DashboardDTO(
                monthlyRevenue,                 
                (int) appointmentsToday,     
                (int) confirmedAppointmentsToday, 
                nextDTO != null ? nextDTO.startTime().toString() : "N/A", 
                nextDTO != null ? nextDTO.serviceName() : "Nenhum",         
                0, 
                upcomingAppointments, 
                Collections.emptyList(), 
                Collections.emptyList(), 
                nextDTO,             
                0 
        );
    }
}