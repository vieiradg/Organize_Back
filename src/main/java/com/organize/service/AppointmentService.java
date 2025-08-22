package com.organize.service;

import com.organize.dto.AppointmentRequestDTO;
import com.organize.model.*;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.EmployeeRepository;
import com.organize.repository.ServiceRepository;
import com.organize.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ServiceRepository serviceRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    public List<Appointment> getAppointmentsByClientAndDateRange(UUID clientId, LocalDateTime start, LocalDateTime end) {        return appointmentRepository.findByClientIdAndStartTimeBetween(clientId, start, end);
    }

    public Appointment createAppointment(AppointmentRequestDTO request) {
        User client = userRepository.findById(request.clientId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        BeautyService service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setService(service);
        appointment.setEmployee(employee);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setStatus(AppointmentStatus.valueOf(request.status()));

        return appointmentRepository.save(appointment);
    }
}
