package com.organize.controller;

import com.organize.dto.EstablishmentDTO;
import com.organize.dto.EstablishmentRequestDTO;
import com.organize.model.Establishment;
import com.organize.model.User;
import com.organize.service.EstablishmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/establishments")
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    public EstablishmentController(EstablishmentService establishmentService) {
        this.establishmentService = establishmentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EstablishmentDTO createEstablishment(
            @RequestBody EstablishmentRequestDTO data,
            @AuthenticationPrincipal User user
    ) {
        var newEstablishment = establishmentService.createEstablishment(data, user);
        return new EstablishmentDTO(newEstablishment.getId(), newEstablishment.getName(), newEstablishment.getAddress(), newEstablishment.getContactPhone());
    }

    @GetMapping
    public List<EstablishmentDTO> getAllEstablishments() {
        return establishmentService.getAllEstablishments().stream()
                .map(establishment -> new EstablishmentDTO(establishment.getId(), establishment.getName(), establishment.getAddress(), establishment.getContactPhone()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstablishmentDTO> getEstablishmentById(@PathVariable UUID id) {
        Establishment establishment = establishmentService.getEstablishmentById(id);
        return ResponseEntity.ok(new EstablishmentDTO(establishment.getId(), establishment.getName(), establishment.getAddress(), establishment.getContactPhone()));
    }
}