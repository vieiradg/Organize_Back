package com.organize.service;

import com.organize.dto.EmployeeRequestDTO;
import com.organize.dto.EmployeeResponseDTO;
import com.organize.model.Employee;
import com.organize.model.Establishment;
import com.organize.model.User;
import com.organize.repository.EmployeeRepository;
import com.organize.repository.UserRepository;
import com.organize.repository.EstablishmentRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeService(UserRepository userRepository,
                           EstablishmentRepository establishmentRepository,
                           EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getEmployeesByEstablishmentId(UUID establishmentId) {
        return employeeRepository.findByEstablishmentId(establishmentId);
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request,
                                   User loggedUser
    ) {
        User user = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Establishment establishment = establishmentRepository.findById(request.establishmentId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));


        if (!establishment.getOwner().equals(loggedUser)) {
            throw new RuntimeException("Você não tem permissão para adicionar funcionários nesse estabelecimento");
        }

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setEstablishment(establishment);
        employee.setName(request.name());
        employee.setRole(request.role());

        Employee saved = employeeRepository.save(employee);
        return toResponseDTO(saved);
    }

    public Employee updateEmployee(UUID employeeId, EmployeeRequestDTO request, User loggedUser) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        if (!employee.getEstablishment().getOwner().equals(loggedUser)) {
            throw new RuntimeException("Você não tem permissão para editar esse funcionário");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Establishment establishment = establishmentRepository.findById(request.establishmentId())
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        employee.setUser(user);
        employee.setEstablishment(establishment);
        employee.setName(request.name());
        employee.setRole(request.role());

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(UUID employeeId, User loggedUser) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        if (!employee.getEstablishment().getOwner().equals(loggedUser)) {
            throw new RuntimeException("Você não tem permissão para excluir esse funcionário");
        }

        employeeRepository.delete(employee);
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getEstablishment().getId(),
                employee.getUser() != null ? employee.getUser().getId() : null,
                employee.getName(),
                employee.getRole(),
                employee.getCreatedAt()
        );
    }

}
