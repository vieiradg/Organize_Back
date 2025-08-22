package com.organize.controller;

import com.organize.dto.ServiceRequestDTO;
import com.organize.dto.ServiceResponseDTO;
import com.organize.model.OfferedService;
import com.organize.model.User;
import com.organize.service.OfferedServiceService;
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

    private final OfferedServiceService offeredServiceService;

    public ServiceController(OfferedServiceService offeredServiceService) {
        this.offeredServiceService = offeredServiceService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ServiceRequestDTO requestDTO
    ) {
        OfferedService newService = offeredServiceService.createService(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ServiceResponseDTO(newService));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getServices(@AuthenticationPrincipal User user) {
        List<OfferedService> services = offeredServiceService.getServicesByOwner(user);
        List<ServiceResponseDTO> serviceDTOs = services.stream()
                .map(ServiceResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(serviceDTOs);
    }
}