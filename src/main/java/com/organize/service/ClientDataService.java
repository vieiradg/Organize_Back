package com.organize.service;

import com.organize.dto.ClientDataRequestDTO;
import com.organize.model.ClientData;
import com.organize.model.Establishment;
import com.organize.model.Role;
import com.organize.model.User;
import com.organize.repository.ClientDataRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ClientDataService {

    private final ClientDataRepository clientDataRepository;
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientDataService(ClientDataRepository clientDataRepository,
                             UserRepository userRepository,
                             EstablishmentRepository establishmentRepository,
                             PasswordEncoder passwordEncoder) { 
        this.clientDataRepository = clientDataRepository;
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClientData createClientData(UUID establishmentId, ClientDataRequestDTO requestDTO, User loggedUser) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para adicionar clientes a este estabelecimento");
        }

        Optional<User> existingUser = userRepository.findByEmail(requestDTO.email());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Já existe um cliente com este email");
        }

        User client = new User();
        client.setName(requestDTO.name());
        client.setEmail(requestDTO.email());
        client.setPhone(requestDTO.phone());

        String rawPassword = requestDTO.phone();
        client.setPassword(passwordEncoder.encode(rawPassword));

        client.setRoles(Set.of(Role.ROLE_CUSTOMER));
        client = userRepository.save(client);

        boolean clientAlreadyExists = clientDataRepository
                .findByEstablishmentId(establishmentId)
                .stream()
                .anyMatch(cd -> cd.getClient().getEmail().equals(requestDTO.email()));

        if (clientAlreadyExists) {
            throw new RuntimeException("Este cliente já possui uma ficha neste estabelecimento");
        }

        ClientData clientData = new ClientData();
        clientData.setClient(client);
        clientData.setEstablishment(establishment);
        clientData.setPrivateNotes(requestDTO.privateNotes());
        clientData.setMissedAppointmentsCount(0);

        return clientDataRepository.save(clientData);
    }


    public List<ClientData> getClientsByEstablishment(UUID establishmentId, User loggedUser) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para visualizar os clientes deste estabelecimento");
        }

        return clientDataRepository.findByEstablishmentId(establishmentId);
    }

    public ClientData updateClientData(UUID establishmentId, UUID clientDataId, ClientDataRequestDTO requestDTO,
            User loggedUser) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para atualizar clientes deste estabelecimento");
        }

        ClientData clientData = clientDataRepository.findById(clientDataId)
                .orElseThrow(() -> new RuntimeException("Ficha do cliente não encontrada"));

        if (!clientData.getEstablishment().getId().equals(establishmentId)) {
            throw new RuntimeException("Esta ficha não pertence ao estabelecimento especificado");
        }

        clientData.setPrivateNotes(requestDTO.privateNotes());

        return clientDataRepository.save(clientData);
    }

    public void deleteClientData(UUID establishmentId, UUID clientDataId, User loggedUser) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para excluir clientes deste estabelecimento");
        }

        ClientData clientData = clientDataRepository.findById(clientDataId)
                .orElseThrow(() -> new RuntimeException("Ficha do cliente não encontrada"));

        if (!clientData.getEstablishment().getId().equals(establishmentId)) {
            throw new RuntimeException("Esta ficha não pertence ao estabelecimento especificado");
        }

        clientDataRepository.delete(clientData);
    }
}