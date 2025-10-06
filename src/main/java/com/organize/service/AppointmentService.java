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
    private final OfferedServiceRepository offeredServiceRepository;
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;
    private final EmployeeRepository employeeRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              OfferedServiceRepository offeredServiceRepository,
                              UserRepository userRepository,
                              EstablishmentRepository establishmentRepository,
                              EmployeeRepository employeeRepository,
                              WebhookRepository webhookRepository,
                              WebhookService webhookService) {

        this.appointmentRepository = appointmentRepository;
        this.offeredServiceRepository = offeredServiceRepository;
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Appointment> getAppointmentsByEstablishmentAndDate(UUID adminId, LocalDateTime start, LocalDateTime end) {
        Establishment establishment = establishmentRepository.findByOwnerId(adminId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado para admin: " + adminId));

        return appointmentRepository.findAppointmentsByEstablishmentAndDateRange(establishment.getId(), start, end);
    }

    // Método para criar agendamento
    public Appointment createAppointment(AppointmentRequestDTO request, User loggedUser) {

        User client = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        OfferedService service = offeredServiceRepository.findById(request.serviceId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Establishment establishment = establishmentRepository.findById(request.establishmentId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

                if (isEmployeeUnavailable(employee.getId(), request.startTime(), request.endTime())) {
                throw new RuntimeException("Funcionário indisponível nesse horário");
                }

        Appointment appointment = new Appointment();
        appointment.setClient(loggedUser);
        appointment.setService(service);
        appointment.setEstablishment(establishment);
        appointment.setEmployee(employee);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setStatus(request.status());
        appointment.setClientNotes(request.clientNotes());

        return appointmentRepository.save(appointment);
    }

    // Recuperar agendamentos de um usuário dentro de um período
    public List<Appointment> getAppointmentsByUserAndDateRange(UUID userId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findAppointmentsByClientAndDateRange(userId, start, end);
    }

    // Método auxiliar para verificar se funcionário está disponível
    public boolean isEmployeeUnavailable(UUID employeeId, LocalDateTime start, LocalDateTime end) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(employeeId, start, end);
        return !conflicts.isEmpty();
    }

    public Appointment updateStatus(UUID appointmentId, String status) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

    // Converter string para AppointmentStatus
    AppointmentStatus newStatus;
    try {
        newStatus = AppointmentStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new RuntimeException("Status inválido");
    }

    appointment.setStatus(newStatus);
    return appointmentRepository.save(appointment);
}

}
