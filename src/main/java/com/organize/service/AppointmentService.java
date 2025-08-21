package com.organize.service;

import com.organize.dto.AppointmentRequestDTO;
import com.organize.model.Appointment;
import com.organize.model.Customer;
import com.organize.model.BeautyService;
import com.organize.model.User;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.CustomerRepository;
import com.organize.repository.ServiceRepository;
import com.organize.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, CustomerRepository customerRepository, ServiceRepository serviceRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    public List<Appointment> getAppointmentsByUserAndDateRange(UUID userId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByUserIdAndStartTimeBetween(userId, start, end);
    }

    public Appointment createAppointment(AppointmentRequestDTO request, User professional) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Set<BeautyService> services = new HashSet<>();
        for (Long serviceId : request.serviceIds()) {
            services.add(serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado")));
        }

        Appointment appointment = new Appointment();
        appointment.setUser(professional);
        appointment.setCustomer(customer);
        appointment.setServices(services);
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setStatus(request.status());

        return appointmentRepository.save(appointment);
    }
}