package com.organize.service;

import com.organize.dto.CustomerResponseDTO;
import com.organize.model.Appointment;
import com.organize.model.Role;
import com.organize.model.User;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public CustomerService(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        List<User> customerUsers = userRepository.findByRolesContaining(Role.ROLE_CUSTOMER);

        return customerUsers.stream().map(user -> {
            List<Appointment> userAppointments = appointmentRepository.findByClient(user);

            LocalDateTime lastVisit = userAppointments.stream()
                    .map(Appointment::getStartTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            Integer appointmentsCount = userAppointments.size();

            return new CustomerResponseDTO(user, lastVisit, appointmentsCount);
        }).collect(Collectors.toList());
    }
}