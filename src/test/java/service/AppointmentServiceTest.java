package service;

import com.organize.dto.AppointmentRequestDTO;
import com.organize.model.*;
import com.organize.repository.*;
import com.organize.service.AppointmentService;
import com.organize.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private OfferedServiceRepository offeredServiceRepository;
    @Mock private UserRepository userRepository;
    @Mock private EstablishmentRepository establishmentRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private WebhookRepository webhookRepository;
    @Mock private WebhookService webhookService;
    @Mock private TransactionsRepository transactionsRepository;

    @InjectMocks private AppointmentService appointmentService;

    private UUID userId, adminId, employeeId, serviceId, appointmentId, establishmentId;
    private User user;
    private Employee employee;
    private OfferedService service;
    private Establishment establishment;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        employeeId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        establishmentId = UUID.randomUUID();

        user = new User(); user.setId(userId); user.setName("Cliente");
        employee = new Employee(); employee.setId(employeeId); employee.setName("Funcionário");
        service = new OfferedService(); service.setId(serviceId); service.setName("Corte"); service.setPriceCents(5000);
        establishment = new Establishment(); establishment.setId(establishmentId); establishment.setName("Barbearia"); establishment.setOwner(user);

        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setClient(user);
        appointment.setEmployee(employee);
        appointment.setService(service);
        appointment.setEstablishment(establishment);
        appointment.setStartTime(LocalDateTime.now().plusDays(1));
        appointment.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        appointment.setStatus(AppointmentStatus.PENDING);
    }

    @Test
    void shouldReturnAppointmentsForUser() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        when(appointmentRepository.findAppointmentsByClientAndDateRange(userId, start, end))
                .thenReturn(List.of(appointment));

        List<Appointment> result = appointmentService.getAppointmentsByUserAndDateRange(userId, start, end);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getClient().getId());
    }

    @Test
    void shouldReturnAppointmentsForEstablishment() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        when(establishmentRepository.findByOwnerId(adminId)).thenReturn(Optional.of(establishment));
        when(appointmentRepository.findAppointmentsByEstablishmentAndDateRange(establishmentId, start, end))
                .thenReturn(List.of(appointment));

        List<Appointment> result = appointmentService.getAppointmentsByEstablishmentAndDate(adminId, start, end);
        assertEquals(1, result.size());
        assertEquals(establishmentId, result.get(0).getEstablishment().getId());
    }

    @Test
    void shouldThrowIfEstablishmentNotFound() {
        when(establishmentRepository.findByOwnerId(adminId)).thenReturn(Optional.empty());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> appointmentService.getAppointmentsByEstablishmentAndDate(adminId, start, end));
        assertTrue(ex.getMessage().contains("Estabelecimento não encontrado"));
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                userId,
                serviceId,
                employeeId,
                establishmentId,
                "Algumas notas",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(offeredServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(establishmentRepository.findById(establishmentId)).thenReturn(Optional.of(establishment));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(appointmentRepository.isEmployeeUnavailable(employeeId, request.startTime(), request.endTime())).thenReturn(false);
        when(appointmentRepository.save(any())).thenReturn(appointment);
        when(webhookRepository.findByEventType("APPOINTMENT_CREATED")).thenReturn(List.of());

        Appointment result = appointmentService.createAppointment(request, user);
        assertNotNull(result);
        assertEquals(userId, result.getClient().getId());
        assertEquals(employeeId, result.getEmployee().getId());
        verify(webhookService).triggerWebhooks(anyList(), anyMap());
    }

    @Test
    void shouldThrowWhenEmployeeUnavailable() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                userId,
                serviceId,
                employeeId,
                establishmentId,
                "Algumas notas",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                null
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(offeredServiceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(establishmentRepository.findById(establishmentId)).thenReturn(Optional.of(establishment));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(appointmentRepository.isEmployeeUnavailable(employeeId, request.startTime(), request.endTime())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> appointmentService.createAppointment(request, user));
        assertEquals("Funcionário já possui agendamento nesse horário", ex.getMessage());
    }

    @Test
    void shouldUpdateAppointmentStatusSuccessfullyAndSaveTransaction() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);
        when(transactionsRepository.save(any())).thenReturn(new Transaction());
        when(webhookRepository.findByUser(user)).thenReturn(List.of());

        Appointment result = appointmentService.updateStatus(appointmentId, "COMPLETED");
        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
        verify(transactionsRepository).save(any());
        verify(webhookService).triggerWebhooks(anyList(), anyMap());
    }

    @Test
    void shouldThrowWhenAppointmentNotFound() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> appointmentService.updateStatus(appointmentId, "COMPLETED"));
        assertTrue(ex.getMessage().contains("Agendamento não encontrado"));
    }

    @Test
    void shouldThrowWhenStatusInvalid() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> appointmentService.updateStatus(appointmentId, "INVALID_STATUS"));
        assertTrue(ex.getMessage().contains("Status inválido"));
    }
}
