package com.organize.controller;

import com.organize.dto.EmployeeRequestDTO;
import com.organize.dto.EmployeeResponseDTO;
import com.organize.model.Employee;
import com.organize.model.User;
import com.organize.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/establishments/{establishmentId}/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @PathVariable UUID establishmentId,
            @RequestBody EmployeeRequestDTO request,
            @AuthenticationPrincipal User loggedUser) {

        EmployeeResponseDTO created = employeeService.createEmployee(establishmentId, request, loggedUser);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployees(
            @PathVariable UUID establishmentId) {

        List<EmployeeResponseDTO> employees = employeeService.getEmployeesByEstablishmentId(establishmentId);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable UUID establishmentId,
            @PathVariable UUID employeeId,
            @RequestBody EmployeeRequestDTO request,
            @AuthenticationPrincipal User loggedUser) {

        EmployeeResponseDTO updated = employeeService.updateEmployee(establishmentId, employeeId, request, loggedUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable UUID employeeId,
            @AuthenticationPrincipal User loggedUser) {

        employeeService.deleteEmployee(employeeId, loggedUser);
        return ResponseEntity.noContent().build();
    }

}
