package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.organize.dto.OfferedServiceRequestDTO;
import com.organize.model.Establishment;
import com.organize.model.OfferedService;
import com.organize.model.User;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.OfferedServiceRepository;
import com.organize.service.OfferedServiceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class OfferedServiceTest {
    @Mock
    private OfferedServiceRepository offeredServiceRepository;

    @Mock
    private EstablishmentRepository establishmentRepository;

    @InjectMocks
    private OfferedServiceService offeredServiceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mustCreateOfferedServiceSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setName("Teste");
        establishment.setOwner(user);

        OfferedServiceRequestDTO dto = new OfferedServiceRequestDTO(
                "Serviço1",
                "Descrição",
                10000,
                60,
                establishmentId
        );

        OfferedService expectedService = new OfferedService();
        expectedService.setName(dto.name());
        expectedService.setDescription(dto.description());
        expectedService.setPriceCents(dto.priceCents());
        expectedService.setDurationMinutes(dto.durationMinutes());
        expectedService.setEstablishment(establishment);

        when(establishmentRepository.findById(establishmentId)).thenReturn(Optional.of(establishment));
        when(offeredServiceRepository.save(any(OfferedService.class))).thenReturn(expectedService);

        OfferedService result = offeredServiceService.createService(dto, user);

        assertNotNull(result);
        assertEquals(dto.name(), result.getName());
        assertEquals(dto.priceCents(), result.getPriceCents());
        assertEquals(establishment, result.getEstablishment());
        verify(establishmentRepository, times(1)).findById(establishmentId);
        verify(offeredServiceRepository, times(1)).save(any(OfferedService.class));
    }

    @Test
    void mustThrowAccessDeniedWhenUserIsNotOwner() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();

        User loggedUser = new User();
        loggedUser.setId(userId);

        User owner  = new User();
        owner.setId(otherUserId);

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(owner);

        OfferedServiceRequestDTO dto = new OfferedServiceRequestDTO(
                "Serviço", "Descrição", 100000, 60, establishmentId
        );

        when(establishmentRepository.findById(establishmentId)).thenReturn(Optional.of(establishment));

        assertThrows(AccessDeniedException.class, () -> offeredServiceService.createService(dto, loggedUser));
        verify(offeredServiceRepository, never()).save(any());
    }

    @Test
    void mustTrhowIllegalArgumentExceptionWhenEstablishmentNotFound() {
        UUID userId = UUID.randomUUID();
        UUID establishmentId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        OfferedServiceRequestDTO dto = new OfferedServiceRequestDTO(
                "Serviço", "Descrição", 100000, 60, establishmentId
        );

        when(establishmentRepository.findById(establishmentId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> offeredServiceService.createService(dto, user));

        assertEquals("Estabelecimento não encontrado", exception.getMessage());
        verify(establishmentRepository, times(1)).findById(establishmentId);
        verify(offeredServiceRepository, never()).save(any());
    }

    @Test
    void mustReturnListOfAllOfferedServicesByEstablishmentIdSuccessfully() {
        UUID establishmentId = UUID.randomUUID();

        Establishment establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setName("Salão da Maria");

        OfferedService s1 = new OfferedService();
        s1.setName("Pintura");
        s1.setEstablishment(establishment);

        OfferedService s2 = new OfferedService();
        s2.setName("Corte");
        s2.setEstablishment(establishment);

        List<OfferedService> mockList = List.of(s1, s2);

        when(establishmentRepository.existsById(establishmentId))
                .thenReturn(Boolean.TRUE);

        when(offeredServiceRepository.findByEstablishmentId(establishmentId))
                .thenReturn(mockList);

        List<OfferedService> result = offeredServiceService.getServicesByEstablishment(establishmentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pintura", result.get(0).getName());
        assertEquals("Corte", result.get(1).getName());

        verify(establishmentRepository, times(1)).existsById(establishmentId);
        verify(offeredServiceRepository, times(1)).findByEstablishmentId(establishmentId);
    }

    @Test
    void shouldThrowExceptionForAllOfferedServicesWhenEstablishmentNotFound() {
        UUID fakeId = UUID.randomUUID();

        when(establishmentRepository.existsById(fakeId))
                .thenReturn(Boolean.FALSE);

        assertThrows(EntityNotFoundException.class, () -> {
            offeredServiceService.getServicesByEstablishment(fakeId);
        });
    }

    @Test
    void mustReturnServiceByIdSuccessfully() {
        UUID serviceId = UUID.randomUUID();

        OfferedService s1 = new OfferedService();
        s1.setId(serviceId);

        when(offeredServiceRepository.findById(serviceId)).thenReturn(Optional.of(s1));
        OfferedService result = offeredServiceService.getServiceById(serviceId);

        assertNotNull(result);
        assertEquals(serviceId, result.getId());
        verify(offeredServiceRepository, times(1)).findById(serviceId);
    }

    @Test
    void mustThrowExceptionWhenServiceNotFound() {
        UUID fakeServiceId = UUID.randomUUID();

        when(offeredServiceRepository.findById(fakeServiceId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> offeredServiceService.getServiceById(fakeServiceId));


        assertEquals("Serviço com ID " + fakeServiceId + " não encontrado.", exception.getMessage());
        verify(offeredServiceRepository, times(1)).findById(fakeServiceId);
    }

    @Test
    void shouldUpdateServiceSuccessfully() {
        UUID serviceId = UUID.randomUUID();

        OfferedService existingService = new OfferedService();
        existingService.setId(serviceId);
        existingService.setName("Corte antigo");
        existingService.setDescription("Descrição antiga");
        existingService.setPriceCents(5000);
        existingService.setDurationMinutes(30);

        OfferedServiceRequestDTO requestDTO = new OfferedServiceRequestDTO(
                "Corte novo",
                "Nova descrição",
                7000,
                45,
                UUID.randomUUID()
        );

        when(offeredServiceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));

        when(offeredServiceRepository.save(existingService)).thenReturn(existingService);

        OfferedService updatedService = offeredServiceService.updateService(serviceId, requestDTO);

        assertNotNull(updatedService);
        assertEquals("Corte novo", updatedService.getName());
        assertEquals("Nova descrição", updatedService.getDescription());
        assertEquals(7000, updatedService.getPriceCents());
        assertEquals(45, updatedService.getDurationMinutes());

        verify(offeredServiceRepository, times(1)).findById(serviceId);
        verify(offeredServiceRepository, times(1)).save(existingService);
    }

    @Test
    void shouldDeleteServiceWhenExists() {
        UUID serviceId = UUID.randomUUID();
        OfferedService existingService = new OfferedService();
        existingService.setId(serviceId);

        when(offeredServiceRepository.existsById(serviceId)).thenReturn(Boolean.TRUE);
        offeredServiceService.deleteService(serviceId);

        verify(offeredServiceRepository, times(1)).existsById(serviceId);
        verify(offeredServiceRepository, times(1)).deleteById(serviceId);
    }

    @Test
    void shouldThrowExceptionWhenServiceNotFound() {
        UUID serviceId = UUID.randomUUID();

        when(offeredServiceRepository.existsById(serviceId)).thenReturn(Boolean.FALSE);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> offeredServiceService.deleteService(serviceId));

        assertEquals("Serviço com ID " + serviceId + " não encontrado.",  exception.getMessage());
        verify(offeredServiceRepository, times(1)).existsById(serviceId);
        verify(offeredServiceRepository, never()).deleteById(serviceId);
    }
}
