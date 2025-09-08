package com.organize.controller;

import com.organize.dto.EstablishmentDTO;
import com.organize.dto.EstablishmentRequestDTO;
import com.organize.model.User;
import com.organize.repository.EstablishmentRepository;
import com.organize.service.EstablishmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/establishments")
public class EstablishmentController {

    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentService establishmentService;

    public EstablishmentController(EstablishmentRepository establishmentRepository, EstablishmentService establishmentService) {
        this.establishmentRepository = establishmentRepository;
        this.establishmentService = establishmentService;
    }

    @GetMapping
    public List<EstablishmentDTO> getAllEstablishments() {
        return establishmentRepository.findAll().stream()
                .map(establishment -> new EstablishmentDTO(establishment.getId(), establishment.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EstablishmentDTO createEstablishment(
            @RequestBody EstablishmentRequestDTO data,
            @AuthenticationPrincipal User user
    ) {
        var newEstablishment = establishmentService.createEstablishment(data, user);
        return new EstablishmentDTO(newEstablishment.getId(), newEstablishment.getName());
    }
}
