package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.organize.model.Establishment;
import com.organize.repository.EstablishmentRepository;
import com.organize.service.EstablishmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class EstablishmentServiceTest {
    @Mock
    private EstablishmentRepository establishmentRepository;

    @InjectMocks
    private EstablishmentService establishmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mustCreateEstablishment() {
        Establishment establishment = new Establishment();
        establishment.setName("Teste");

        when(establishmentRepository.save(any(Establishment.class))).thenReturn(establishment);
        Establishment savedEstablishment = establishmentRepository.save(establishment);

        assertNotNull(savedEstablishment);
        assertEquals(savedEstablishment.getName(), establishment.getName());
        verify(establishmentRepository, times(1)).save(establishment);
    }

    @Test
    void mustReturnEstablishmentsByIdWhenIsValid() {
        UUID id = UUID.randomUUID();
        Establishment establishment = new Establishment();
        establishment.setId(id);

        when(establishmentRepository.findById(id)).thenReturn(Optional.of(establishment));
        Establishment savedEstablishment = establishmentRepository.findById(id).get();

        assertNotNull(savedEstablishment);
        assertEquals(savedEstablishment.getId(), establishment.getId());
        verify(establishmentRepository, times(1)).findById(id);
    }

    @Test
    void mustReturnExceptionWhenEstablishmentNotFound() {
        UUID id = UUID.randomUUID();

        when(establishmentRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> establishmentService.getEstablishmentById(id)
        );
        assertEquals("Estabelecimento com ID " + id + " não encontrado.", exception.getMessage());
        verify(establishmentRepository, times(1)).findById(id);
    }

    @Test
    void mustReturnListOfAllEstablishments() {
        Establishment e1 = new Establishment();
        e1.setId(UUID.randomUUID());
        e1.setName("Teste1");

        Establishment e2 = new Establishment();
        e2.setId(UUID.randomUUID());
        e2.setName("Teste2");

        List<Establishment> mockList = List.of(e1, e2);
        when(establishmentRepository.findAll()).thenReturn(mockList);

        List<Establishment> result = establishmentService.getAllEstablishments();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Teste1", result.get(0).getName());
        assertEquals("Teste2", result.get(1).getName());
        verify(establishmentRepository, times(1)).findAll();
    }

    @Test
    void mustReturnEmpityListOfAllEstablishments() {
        when(establishmentRepository.findAll()).thenReturn(Collections.emptyList());

        List<Establishment> result = establishmentService.getAllEstablishments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(establishmentRepository, times(1)).findAll();
    }
}
