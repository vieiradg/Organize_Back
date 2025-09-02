package com.organize.controller;

import com.organize.dto.OfferedServiceRequestDTO;
import com.organize.dto.OfferedServiceResponseDTO;
import com.organize.model.OfferedService;
import com.organize.model.User;
import com.organize.service.OfferedServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/establishments/{establishmentId}/services")
public class OfferedServiceController {

    private final OfferedServiceService offeredServiceService;

    public OfferedServiceController(OfferedServiceService offeredServiceService) {
        this.offeredServiceService = offeredServiceService;
    }

    @PostMapping
    public ResponseEntity<OfferedServiceResponseDTO> createService(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid OfferedServiceRequestDTO requestDTO
    ) {
        OfferedService newService = offeredServiceService.createService(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OfferedServiceResponseDTO(newService));
    }

    // READ (All)
    @GetMapping
    public ResponseEntity<List<OfferedServiceResponseDTO>> getAllServicesByEstablishment(
            @PathVariable UUID establishmentId
    ) {
        List<OfferedServiceResponseDTO> services = offeredServiceService.getServicesByEstablishment(establishmentId).stream()
                .map(OfferedServiceResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }

    // READ (Single)
    @GetMapping("/{serviceId}")
    public ResponseEntity<OfferedServiceResponseDTO> getServiceById(
            @PathVariable UUID establishmentId, // Mantemos para consistência da rota, embora não seja usado no service
            @PathVariable UUID serviceId
    ) {
        OfferedService service = offeredServiceService.getServiceById(serviceId);
        return ResponseEntity.ok(new OfferedServiceResponseDTO(service));
    }

    // UPDATE
    @PutMapping("/{serviceId}")
    public ResponseEntity<OfferedServiceResponseDTO> updateService(
            @PathVariable UUID establishmentId,
            @PathVariable UUID serviceId,
            @RequestBody @Valid OfferedServiceRequestDTO requestDTO
    ) {
        OfferedService updatedService = offeredServiceService.updateService(serviceId, requestDTO);
        return ResponseEntity.ok(new OfferedServiceResponseDTO(updatedService));
    }

    // DELETE
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(
            @PathVariable UUID establishmentId,
            @PathVariable UUID serviceId
    ) {
        offeredServiceService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }
}