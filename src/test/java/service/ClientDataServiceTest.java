package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.organize.dto.ClientDataRequestDTO;
import com.organize.model.ClientData;
import com.organize.model.Establishment;
import com.organize.model.User;
import com.organize.repository.ClientDataRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.UserRepository;
import com.organize.service.ClientDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class ClientDataServiceTest {
    @Mock
    private ClientDataRepository clientDataRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EstablishmentRepository establishmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientDataService service;


    private Establishment establishment;
    private UUID establishmentId =  UUID.randomUUID();

    private User user;
    private UUID userId = UUID.randomUUID();

    private ClientDataRequestDTO dto = new ClientDataRequestDTO(
            "Teste1",
            "teste@email.com",
            "31900000000",
            "Notas Teste"
    );

    private User client = new User();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(userId);
        user.setEmail("teste@email.com");

        establishment = new Establishment();
        establishment.setId(establishmentId);
        establishment.setOwner(user);

        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setPhone(dto.phone());

        String rawPassword = dto.phone();
        client.setPassword(passwordEncoder.encode(rawPassword));
    }

    @Test
    void mustCreateClientData() {
        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(client);

        when(clientDataRepository.findByEstablishmentId(establishmentId))
                .thenReturn(Collections.emptyList());

        when(clientDataRepository.save(any(ClientData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.createClientData(establishmentId, dto, user).getClient();

        assertNotNull(result);
        assertEquals(client, result);

        verify(clientDataRepository, times(1))
                .findByEstablishmentId(establishmentId);
        verify(clientDataRepository, times(1))
                .save(any(ClientData.class));
    }

    @Test
    void shouldThrowWhenEstablishmentNotFound() {
        UUID fakeId = UUID.randomUUID();

        when(establishmentRepository.findById(fakeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.createClientData(fakeId, dto, user));

        assertEquals("Estabelecimento não encontrado", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUserNotOwner() {
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(otherUserId);

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.createClientData(establishmentId, dto, otherUser));

        assertEquals("Você não tem permissão para adicionar clientes a este estabelecimento", exception.getMessage());
    }

    @Test
    void shouldReturnClientsSuccessfully() {
        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));
        ClientData clientData = new ClientData();
        clientData.setClient(client);
        clientData.setEstablishment(establishment);

        when(clientDataRepository.findByEstablishmentId(establishmentId))
                .thenReturn(Collections.singletonList(clientData));

        List<ClientData> result = service.getClientsByEstablishment(establishmentId, user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(client, result.get(0).getClient());
    }

    @Test
    void shouldThrowWhenGetClientsEstablishmentNotFound() {
        UUID fakeId = UUID.randomUUID();

        when(establishmentRepository.findById(fakeId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.getClientsByEstablishment(fakeId, user));

        assertEquals("Estabelecimento não encontrado", exception.getMessage());
    }

    @Test
    void shouldThrowWhenGetClientsUserNotOwner() {
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(otherUserId);

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.getClientsByEstablishment(establishmentId, otherUser));

        assertEquals("Você não tem permissão para visualizar os clientes deste estabelecimento", exception.getMessage());
    }

    @Test
    void shouldUpdateClientDataSuccessfully() {
        UUID clientDataId = UUID.randomUUID();
        ClientData clientData = new ClientData();
        clientData.setClient(client);
        clientData.setEstablishment(establishment);
        clientData.setPrivateNotes("Old notes");

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));
        when(clientDataRepository.findById(clientDataId))
                .thenReturn(Optional.of(clientData));
        when(clientDataRepository.save(any(ClientData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClientDataRequestDTO updateDto = new ClientDataRequestDTO(
                client.getName(), client.getEmail(), client.getPhone(), "Updated notes"
        );

        ClientData result = service.updateClientData(establishmentId, clientDataId, updateDto, user);

        assertNotNull(result);
        assertEquals("Updated notes", result.getPrivateNotes());
    }


    @Test
    void shouldThrowWhenUpdateEstablishmentNotFound() {
        UUID clientDataId = UUID.randomUUID();

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.updateClientData(establishmentId, clientDataId, dto, user));

        assertEquals("Estabelecimento não encontrado", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUpdateUserNotOwner() {
        UUID clientDataId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(otherUserId);

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.updateClientData(establishmentId, clientDataId, dto, otherUser));

        assertEquals("Você não tem permissão para atualizar clientes deste estabelecimento", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUpdateClientDataNotFound() {
        UUID clientDataId = UUID.randomUUID();

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        when(clientDataRepository.findById(clientDataId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.updateClientData(establishmentId, clientDataId, dto, user));

        assertEquals("Ficha do cliente não encontrada", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUpdateClientDataNotBelongToEstablishment() {
        UUID clientDataId = UUID.randomUUID();
        Establishment otherEstablishment = new Establishment();
        otherEstablishment.setId(UUID.randomUUID());
        ClientData clientData = new ClientData();
        clientData.setEstablishment(otherEstablishment);

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        when(clientDataRepository.findById(clientDataId))
                .thenReturn(Optional.of(clientData));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.updateClientData(establishmentId, clientDataId, dto, user));

        assertEquals("Esta ficha não pertence ao estabelecimento especificado", exception.getMessage());
    }


    @Test
    void shouldThrowWhenDeleteEstablishmentNotFound() {
        UUID clientDataId = UUID.randomUUID();

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.deleteClientData(establishmentId, clientDataId, user));

        assertEquals("Estabelecimento não encontrado", exception.getMessage());
    }

    @Test
    void shouldThrowWhenDeleteUserNotOwner() {
        UUID clientDataId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(otherUserId);

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.deleteClientData(establishmentId, clientDataId, otherUser));

        assertEquals("Você não tem permissão para excluir clientes deste estabelecimento", exception.getMessage());
    }

    @Test
    void shouldDeleteClientDataSuccessfully() {
        UUID clientDataId = UUID.randomUUID();
        ClientData clientData = new ClientData();
        clientData.setClient(client);
        clientData.setEstablishment(establishment);

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));
        when(clientDataRepository.findById(clientDataId))
                .thenReturn(Optional.of(clientData));

        service.deleteClientData(establishmentId, clientDataId, user);

        verify(clientDataRepository, times(1)).delete(clientData);
    }


    @Test
    void shouldThrowWhenDeleteClientDataNotFound() {
        UUID clientDataId = UUID.randomUUID();

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        when(clientDataRepository.findById(clientDataId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.deleteClientData(establishmentId, clientDataId, user));

        assertEquals("Ficha do cliente não encontrada", exception.getMessage());
    }

    @Test
    void shouldThrowWhenDeleteClientDataNotBelongToEstablishment() {
        UUID clientDataId = UUID.randomUUID();
        Establishment otherEstablishment = new Establishment();
        otherEstablishment.setId(UUID.randomUUID()); 
        ClientData clientData = new ClientData();
        clientData.setEstablishment(otherEstablishment);

        when(establishmentRepository.findById(establishmentId))
                .thenReturn(Optional.of(establishment));

        when(clientDataRepository.findById(clientDataId))
                .thenReturn(Optional.of(clientData));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.deleteClientData(establishmentId, clientDataId, user));

        assertEquals("Esta ficha não pertence ao estabelecimento especificado", exception.getMessage());
    }


}
