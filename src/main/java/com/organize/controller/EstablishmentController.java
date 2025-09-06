package com.organize.controller;

import com.organize.dto.EstablishmentDTO;
import com.organize.model.Establishment;
import com.organize.repository.EstablishmentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/establishments")
public class EstablishmentController {

    private final EstablishmentRepository establishmentRepository;

    public EstablishmentController(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    @GetMapping
    public List<EstablishmentDTO> getAllEstablishments() {
        return establishmentRepository.findAll().stream()
                .map(establishment -> new EstablishmentDTO(establishment.getId(), establishment.getName()))
                .collect(Collectors.toList());
    }
}
