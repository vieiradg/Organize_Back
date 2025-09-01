package com.organize.controller;

import com.organize.dto.EmployeeRequestDTO;
import com.organize.dto.EmployeeResponseDTO;
import com.organize.model.Employee;
import com.organize.model.User;
import com.organize.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/establishments")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Estabelecimentos precisa estar pronto para prosseguir

//    @PostMapping
//    public ResponseEntity<EmployeeResponseDTO> createEmployee(
//            @PathVariable UUID establishmentId,
//            @RequestBody EmployeeRequestDTO request,
//            @AuthenticationPrincipal User loggedUser
//    ){
//
//        EmployeeResponseDTO created = employeeService.createEmployee(request, loggedUser);
//        return ResponseEntity.ok(created);
//    }

}
