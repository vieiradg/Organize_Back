package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.organize.dto.EmployeeRequestDTO;
import com.organize.dto.EmployeeResponseDTO;
import com.organize.model.Employee;
import com.organize.model.Establishment;
import com.organize.model.User;
import com.organize.repository.EmployeeRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.UserRepository;
import com.organize.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class EmployeeServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EstablishmentRepository estRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mustGetEmployeesByEstablishmentId() {
        UUID establishmentId = UUID.randomUUID();
        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);

        Employee e1 = new Employee();
        e1.setName("John");
        e1.setEstablishment(establishment);

        Employee e2 = new Employee();
        e2.setName("Jane");
        e2.setEstablishment(establishment);

        List<Employee> mockList = List.of(e1, e2);

        when(employeeRepository.findByEstablishmentId(establishmentId)).thenReturn(mockList);
        List<EmployeeResponseDTO> result = service.getEmployeesByEstablishmentId(establishmentId);

        assertEquals(mockList.size(), result.size());
        assertEquals("John", result.get(0).name());
        assertEquals("Jane", result.get(1).name());
        verify(employeeRepository, times(1)).findByEstablishmentId(establishmentId);

    }

    @Test
    void mustCreateEmployeeSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        User loggedUser = user;

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(loggedUser);

        EmployeeRequestDTO request = new EmployeeRequestDTO(
                userId, establishmentId, "Empregado", "Atendente"
        );

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setUser(user);
        employee.setEstablishment(establishment);
        employee.setName(request.name());
        employee.setRole(request.role());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(estRepository.findById(establishmentId)).thenReturn(Optional.of(establishment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO savedEmployee = service.createEmployee(establishmentId, request, loggedUser);

        assertEquals("Empregado", savedEmployee.name());
        assertEquals("Atendente", savedEmployee.role());
        assertEquals(userId, savedEmployee.userId());
        assertEquals(establishmentId, savedEmployee.establishmentId());

        verify(userRepository, times(1)).findById(userId);
        verify(estRepository, times(1)).findById(establishmentId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
       UUID fakeUserId = UUID.randomUUID();
       UUID establishmentId = UUID.randomUUID();

       User loggedUser = new User();
       loggedUser.setId(UUID.randomUUID());

       EmployeeRequestDTO request = new EmployeeRequestDTO(
              fakeUserId, establishmentId, "Empregado", "Atendente"
       );

       Establishment establishment = new Establishment();
       establishment.setId(establishmentId);
       establishment.setOwner(loggedUser);

       when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());
       when(estRepository.findById(establishmentId)).thenReturn(Optional.of(establishment));

       RuntimeException exception = assertThrows(RuntimeException.class, () -> service.createEmployee(establishmentId, request, loggedUser));

       assertEquals("Usuário não encontrado", exception.getMessage());
       verify(userRepository, times(1)).findById(fakeUserId);
       verify(estRepository, never()).findById(any());
       verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEstablishmentNotFound() {
        UUID userId = UUID.randomUUID();
        UUID fakeEstablishmentId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();

        User loggedUser = new User();
        loggedUser.setId(userId);

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(loggedUser);

        EmployeeRequestDTO request = new EmployeeRequestDTO(
                userId, fakeEstablishmentId, "Empregado", "Atendente"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(loggedUser));
        when(estRepository.findById(fakeEstablishmentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.createEmployee(fakeEstablishmentId, request, loggedUser));

        assertEquals("Estabelecimento não encontrado", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(estRepository, times(1)).findById(fakeEstablishmentId);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void mustDeleteEmployeeById() {
        UUID userId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();

        User loggedUser = new User();
        loggedUser.setId(userId);

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(loggedUser);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEstablishment(establishment);
        employee.setUser(loggedUser);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        service.deleteEmployee(employeeId, loggedUser);

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void mustThrowExceptionWhenEmployeeNotFound() {
        UUID employeeId = UUID.randomUUID();
        User loggedUser = new User();
        loggedUser.setId(UUID.randomUUID());

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.deleteEmployee(employeeId, loggedUser));

        assertEquals("Funcionário não encontrado", exception.getMessage());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void mustThrowExceptionWhenUserNotOwner() {
        UUID employeeId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID loggedUserId = UUID.randomUUID();

        User owner = new User();
        owner.setId(ownerId);

        Establishment establishment = new Establishment();
        establishment.setOwner(owner);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEstablishment(establishment);

        User loggedUser = new User();
        loggedUser.setId(loggedUserId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.deleteEmployee(employeeId, loggedUser));

        assertEquals("Você não tem permissão para excluir esse funcionário", exception.getMessage());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any());
    }

    @Test
    void mustUpdateEmployeeSuccessfully() {
        UUID employeeId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User loggedUser = new User();
        loggedUser.setId(userId);

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(loggedUser);

        User userToAssign = new User();
        userToAssign.setId(userId);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEstablishment(establishment);
        employee.setUser(loggedUser);

        EmployeeRequestDTO request = new EmployeeRequestDTO(userId, establishmentId, "Novo Nome", "Atendente");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToAssign));
        when(estRepository.findById(establishmentId)).thenReturn(Optional.of(establishment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO updatedEmployee = service.updateEmployee(establishmentId, employeeId, request, loggedUser);

        assertEquals("Novo Nome", updatedEmployee.name());
        assertEquals("Atendente", updatedEmployee.role());
        assertEquals(userId, updatedEmployee.userId());
        assertEquals(establishmentId, updatedEmployee.establishmentId());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(userRepository, times(1)).findById(userId);
        verify(estRepository, times(1)).findById(establishmentId);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void mustThrowExceptionWhenEmployeeNotFoundOnUpdate() {
        UUID employeeId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User loggedUser = new User();
        loggedUser.setId(userId);

        EmployeeRequestDTO request = new EmployeeRequestDTO(userId, establishmentId, "Nome", "Cargo");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateEmployee(establishmentId, employeeId, request, loggedUser));

        assertEquals("Funcionário não encontrado", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void mustThrowExceptionWhenUserNotOwnerOnUpdate() {
        UUID employeeId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID loggedUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User owner = new User();
        owner.setId(ownerId);

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(owner);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEstablishment(establishment);

        User loggedUser = new User();
        loggedUser.setId(loggedUserId);

        EmployeeRequestDTO request = new EmployeeRequestDTO(userId, establishmentId, "Nome", "Cargo");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateEmployee(establishmentId, employeeId, request, loggedUser));

        assertEquals("Você não tem permissão para editar esse funcionário", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void mustThrowExceptionWhenUserNotFoundOnUpdate() {
        UUID employeeId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();
        UUID loggedUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User loggedUser = new User();
        loggedUser.setId(loggedUserId);

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(loggedUser);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEstablishment(establishment);

        EmployeeRequestDTO request = new EmployeeRequestDTO(userId, establishmentId, "Nome", "Cargo");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateEmployee(establishmentId, employeeId, request, loggedUser));

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(userRepository, times(1)).findById(userId);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void mustThrowExceptionWhenEstablishmentNotFoundOnUpdate() {
        UUID employeeId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();
        UUID loggedUserId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User loggedUser = new User();
        loggedUser.setId(loggedUserId);

        Establishment anyEstablishment = new Establishment();
        anyEstablishment.setOwner(loggedUser);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEstablishment(anyEstablishment);

        EmployeeRequestDTO request = new EmployeeRequestDTO(userId, establishmentId, "Nome", "Cargo");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(userRepository.findById(userId)).thenReturn(Optional.of(loggedUser));
        when(estRepository.findById(establishmentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updateEmployee(establishmentId, employeeId, request, loggedUser));

        assertEquals("Estabelecimento não encontrado", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(userRepository, times(1)).findById(userId);
        verify(estRepository, times(1)).findById(establishmentId);
        verify(employeeRepository, never()).save(any());
    }
}
