package com.organize.controller;

import com.organize.dto.ServiceRequestDTO;
import com.organize.dto.ServiceResponseDTO;
import com.organize.model.BeautyService;
import com.organize.model.User;
import com.organize.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ServiceRequestDTO requestDTO
    ) {
        BeautyService newService = serviceService.createService(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ServiceResponseDTO(newService));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getServices(@AuthenticationPrincipal User user) {
        List<BeautyService> services = serviceService.getServicesByUser(user);
        List<ServiceResponseDTO> serviceDTOs = services.stream()
                .map(ServiceResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(serviceDTOs);
    }
}