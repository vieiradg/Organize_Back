package com.organize.controller;

import com.organize.dto.AppointmentDTO;
import com.organize.dto.AppointmentRequestDTO;
import com.organize.dto.AppointmentStatusUpdateDTO;
import com.organize.model.Appointment;
import com.organize.model.Role;
import com.organize.model.User;
import com.organize.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAppointments(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Appointment> appointments;

        if (user.getRoles().contains(Role.ROLE_ADMIN)) {
            appointments = appointmentService.getAppointmentsByEstablishmentAndDate(
                    user.getId(),
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            );
        } else {
            appointments = appointmentService.getAppointmentsByUserAndDateRange(
                    user.getId(),
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            );
        }

        return ResponseEntity.ok(
                appointments.stream().map(AppointmentDTO::new).toList()
        );
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid AppointmentRequestDTO requestDTO
    ) {
        Appointment newAppointment = appointmentService.createAppointment(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AppointmentDTO(newAppointment));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable UUID id,
            @RequestBody @Valid AppointmentStatusUpdateDTO request
    ) {
        Appointment updated = appointmentService.updateStatus(id, request.status());
        return ResponseEntity.ok(new AppointmentDTO(updated));
    }

}
