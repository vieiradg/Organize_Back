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

        Appointment nextAppointment = appointmentRepository
                .findTopByClientIdAndStartTimeAfterAndStatusOrderByStartTimeAsc(
                        clientId,
                        now,
                        AppointmentStatus.CONFIRMED
                )
                .orElse(null);

        long totalAppointments = appointmentRepository.countByClientId(clientId);

        List<AppointmentDTO> upcomingAppointments = appointmentRepository
                .findByClientIdAndStartTimeAfterOrderByStartTimeAsc(clientId, now)
                .stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        AppointmentDTO nextDTO = nextAppointment != null ? new AppointmentDTO(nextAppointment) : null;

        return new DashboardDTO(
                0,                   
                0,                    
                0,                 
                nextDTO != null ? nextDTO.startTime().toString() : null, 
                nextDTO != null ? nextDTO.serviceName() : null,         
                0,                   
                upcomingAppointments, 
                Collections.emptyList(),
                Collections.emptyList(), 
                nextDTO,             
                totalAppointments    
        );
    }
}