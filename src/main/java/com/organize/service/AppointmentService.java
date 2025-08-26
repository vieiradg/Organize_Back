package com.organize.service;

import com.organize.dto.AppointmentRequestDTO;
import com.organize.model.*;
import com.organize.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;
    private final EstablishmentRepository establishmentRepository;

    public AppointmentService(
        AppointmentRepository appointmentRepository,
        UserRepository userRepository,
        ServiceRepository serviceRepository,
        EmployeeRepository employeeRepository,
        EstablishmentRepository establishmentRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.employeeRepository = employeeRepository;
        this.establishmentRepository = establishmentRepository;
    }

    public List<Appointment> getAppointmentsByEmployeeAndDateRange(UUID employeeId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByEmployeeIdAndStartTimeBetween(employeeId, start, end);
    }

    public Appointment createAppointment(AppointmentRequestDTO request, User userLogged) {
        User client = userRepository.findById(request.clientId())
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

 
        Employee employee = employeeRepository.findById(request.employeeId())
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        Establishment establishment = establishmentRepository.findById(request.establishmentId())
            .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setEmployee(employee);
        appointment.setEstablishment(establishment);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setStatus(AppointmentStatus.valueOf(request.status().toUpperCase()));

        return appointmentRepository.save(appointment);
    }
}
