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
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;
    private final EmployeeRepository employeeRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ServiceRepository serviceRepository, UserRepository userRepository, EstablishmentRepository establishmentRepository, EmployeeRepository employeeRepository) {
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Appointment> getAppointmentsByUserAndDateRange(UUID userId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findAppointmentsByClientAndDateRange(userId, start, end);
    }


    public Appointment createAppointment(AppointmentRequestDTO request, User loggedUser) {

        User client = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        OfferedService service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Establishment establishment = establishmentRepository.findById(request.establishmentId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        Appointment appointment = new Appointment();
        appointment.setClient(loggedUser);
        appointment.setService(service);
        appointment.setEstablishment(establishment);
        appointment.setEmployee(employee);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setStatus(AppointmentStatus.valueOf(request.status().toUpperCase()));
        appointment.setClientNotes(request.clientNotes());

        return appointmentRepository.save(appointment);
    }


}