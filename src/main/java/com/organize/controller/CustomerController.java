package com.organize.controller;

import com.organize.dto.CustomerRequestDTO;
import com.organize.dto.CustomerResponseDTO;
import com.organize.model.Customer;
import com.organize.model.User;
import com.organize.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CustomerRequestDTO requestDTO
    ) {
        Customer newCustomer = customerService.createCustomer(requestDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomerResponseDTO(newCustomer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getCustomers(@AuthenticationPrincipal User user) {
        List<Customer> customers = customerService.getCustomersByUser(user);
        List<CustomerResponseDTO> customerDTOs = customers.stream()
                .map(CustomerResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customerDTOs);
    }
}
