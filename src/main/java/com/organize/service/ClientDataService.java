package com.organize.service;

import com.organize.dto.ClientDataRequestDTO;
import com.organize.model.ClientData;
import com.organize.model.Establishment;
import com.organize.model.User;
import com.organize.repository.ClientDataRepository;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientDataService {

    private final ClientDataRepository clientDataRepository;
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;

    public ClientDataService(ClientDataRepository clientDataRepository,
            UserRepository userRepository,
            EstablishmentRepository establishmentRepository) {
        this.clientDataRepository = clientDataRepository;
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
    }

    public ClientData createClientData(UUID establishmentId, ClientDataRequestDTO requestDTO, User loggedUser) {
        // Buscar o estabelecimento
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        // Verificar se o usuário logado é o dono do estabelecimento
        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para adicionar clientes a este estabelecimento");
        }

        // Buscar o cliente (usuário)
        User client = userRepository.findById(requestDTO.clientId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Verificar se já existe uma ficha para este cliente neste estabelecimento
        List<ClientData> existingClientData = clientDataRepository.findByEstablishmentId(establishmentId);
        boolean clientAlreadyExists = existingClientData.stream()
                .anyMatch(cd -> cd.getClient().getId().equals(requestDTO.clientId()));

        if (clientAlreadyExists) {
            throw new RuntimeException("Este cliente já possui uma ficha neste estabelecimento");
        }

        // Criar nova ficha de cliente
        ClientData clientData = new ClientData();
        clientData.setClient(client);
        clientData.setEstablishment(establishment);
        clientData.setPrivateNotes(requestDTO.privateNotes());
        clientData.setMissedAppointmentsCount(0);

        return clientDataRepository.save(clientData);
    }

    public List<ClientData> getClientsByEstablishment(UUID establishmentId, User loggedUser) {
        // Buscar o estabelecimento
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        // Verificar se o usuário logado é o dono do estabelecimento
        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para visualizar os clientes deste estabelecimento");
        }

        return clientDataRepository.findByEstablishmentId(establishmentId);
    }

    public ClientData updateClientData(UUID establishmentId, UUID clientDataId, ClientDataRequestDTO requestDTO,
            User loggedUser) {
        // Buscar o estabelecimento
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        // Verificar se o usuário logado é o dono do estabelecimento
        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para atualizar clientes deste estabelecimento");
        }

        // Buscar a ficha do cliente
        ClientData clientData = clientDataRepository.findById(clientDataId)
                .orElseThrow(() -> new RuntimeException("Ficha do cliente não encontrada"));

        // Verificar se a ficha pertence ao estabelecimento correto
        if (!clientData.getEstablishment().getId().equals(establishmentId)) {
            throw new RuntimeException("Esta ficha não pertence ao estabelecimento especificado");
        }

        // Atualizar as notas privadas
        clientData.setPrivateNotes(requestDTO.privateNotes());

        return clientDataRepository.save(clientData);
    }

    public void deleteClientData(UUID establishmentId, UUID clientDataId, User loggedUser) {
        // Buscar o estabelecimento
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new RuntimeException("Estabelecimento não encontrado"));

        // Verificar se o usuário logado é o dono do estabelecimento
        if (!establishment.getOwner().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para excluir clientes deste estabelecimento");
        }

        // Buscar a ficha do cliente
        ClientData clientData = clientDataRepository.findById(clientDataId)
                .orElseThrow(() -> new RuntimeException("Ficha do cliente não encontrada"));

        // Verificar se a ficha pertence ao estabelecimento correto
        if (!clientData.getEstablishment().getId().equals(establishmentId)) {
            throw new RuntimeException("Esta ficha não pertence ao estabelecimento especificado");
        }

        clientDataRepository.delete(clientData);
    }
}