package service;

import com.organize.dto.CustomerResponseDTO;
import com.organize.model.Appointment;
import com.organize.model.Role;
import com.organize.model.User;
import com.organize.repository.AppointmentRepository;
import com.organize.repository.UserRepository;
import com.organize.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private CustomerService customerService;

    private User customerUser;
    private User nonCustomerUser;
    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    void setUp() {
        customerUser = new User();
        customerUser.setId(UUID.randomUUID());
        customerUser.setName("Maria Cliente");
        customerUser.setRoles(Set.of(Role.ROLE_CUSTOMER));

        nonCustomerUser = new User();
        nonCustomerUser.setId(UUID.randomUUID());
        nonCustomerUser.setName("Carlos Admin");
        nonCustomerUser.setRoles(Set.of(Role.ROLE_ADMIN));

        appointment1 = new Appointment();
        appointment1.setClient(customerUser);
        appointment1.setStartTime(LocalDateTime.of(2024, 10, 10, 10, 0));

        appointment2 = new Appointment();
        appointment2.setClient(customerUser);
        appointment2.setStartTime(LocalDateTime.of(2024, 12, 25, 14, 0));
    }

    @Test
    void shouldReturnAllCustomersWithLastVisitAndAppointmentCount() {
        when(userRepository.findByRolesContaining(Role.ROLE_CUSTOMER))
                .thenReturn(List.of(customerUser));
        when(appointmentRepository.findByClient(customerUser))
                .thenReturn(List.of(appointment1, appointment2));

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        CustomerResponseDTO dto = result.get(0);

        assertEquals(customerUser.getName(), dto.name());
        assertEquals(2, dto.appointmentsCount());
        assertEquals(LocalDateTime.of(2024, 12, 25, 14, 0), dto.lastVisit());
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomersFound() {
        when(userRepository.findByRolesContaining(Role.ROLE_CUSTOMER))
                .thenReturn(List.of());

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentRepository, never()).findByClient(any());
    }

    @Test
    void shouldHandleCustomerWithoutAppointments() {
        when(userRepository.findByRolesContaining(Role.ROLE_CUSTOMER))
                .thenReturn(List.of(customerUser));
        when(appointmentRepository.findByClient(customerUser))
                .thenReturn(List.of());

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertNull(result.get(0).lastVisit());
        assertEquals(0, result.get(0).appointmentsCount());
    }

    @Test
    void shouldReturnCustomerByIdWithAppointments() {
        UUID id = customerUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(customerUser));
        when(appointmentRepository.findByClient(customerUser))
                .thenReturn(List.of(appointment1, appointment2));

        Optional<CustomerResponseDTO> resultOpt = customerService.getCustomerById(id);

        assertTrue(resultOpt.isPresent());
        CustomerResponseDTO result = resultOpt.get();

        assertEquals(customerUser.getName(), result.name());
        assertEquals(2, result.appointmentsCount());
        assertEquals(LocalDateTime.of(2024, 12, 25, 14, 0), result.lastVisit());
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<CustomerResponseDTO> result = customerService.getCustomerById(id);

        assertTrue(result.isEmpty());
        verify(appointmentRepository, never()).findByClient(any());
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserIsNotCustomer() {
        UUID id = nonCustomerUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(nonCustomerUser));

        Optional<CustomerResponseDTO> result = customerService.getCustomerById(id);

        assertTrue(result.isEmpty());
        verify(appointmentRepository, never()).findByClient(any());
    }

    @Test
    void shouldReturnCustomerWithNoAppointments() {
        UUID id = customerUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(customerUser));
        when(appointmentRepository.findByClient(customerUser)).thenReturn(List.of());

        Optional<CustomerResponseDTO> resultOpt = customerService.getCustomerById(id);

        assertTrue(resultOpt.isPresent());
        CustomerResponseDTO dto = resultOpt.get();
        assertEquals(0, dto.appointmentsCount());
        assertNull(dto.lastVisit());
    }
}
