package com.organize.controller;

import com.organize.dto.AppointmentDTO;
import com.organize.dto.AppointmentRequestDTO;
import com.organize.model.Appointment;
import com.organize.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAppointments(
            @RequestParam UUID clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Appointment> appointments = appointmentService.getAppointmentsByClientAndDateRange(
                clientId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );

        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointmentDTOs);
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(
            @RequestBody @Valid AppointmentRequestDTO requestDTO
    ) {
        try {
            Appointment newAppointment = appointmentService.createAppointment(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AppointmentDTO(newAppointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
