package com.organize.service;

import com.organize.dto.OfferedServiceRequestDTO;
import com.organize.model.Establishment;
import com.organize.model.OfferedService;
import com.organize.model.User;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.OfferedServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OfferedServiceService {

    private final OfferedServiceRepository offeredServiceRepository;
    private final EstablishmentRepository establishmentRepository;

    public OfferedServiceService(OfferedServiceRepository offeredServiceRepository, EstablishmentRepository establishmentRepository) {
        this.offeredServiceRepository = offeredServiceRepository;
        this.establishmentRepository = establishmentRepository;
    }

    public OfferedService createService(OfferedServiceRequestDTO requestDTO, User user) {
        Establishment establishment = establishmentRepository.findById(requestDTO.establishmentId())
                .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado"));

        OfferedService service = new OfferedService();
        service.setName(requestDTO.name());
        service.setDescription(requestDTO.description());
        service.setPriceCents(requestDTO.priceCents());
        service.setDurationMinutes(requestDTO.duration());
        service.setEstablishment(establishment);

        return offeredServiceRepository.save(service);
    }

    public List<OfferedService> getServicesByOwner(User owner) {
        return offeredServiceRepository.findServicesByOwner(owner);
    }

    public List<OfferedService> getServicesByEstablishment(UUID establishmentId) {
        if (!establishmentRepository.existsById(establishmentId)) {
            throw new EntityNotFoundException("Estabelecimento com ID " + establishmentId + " não encontrado.");
        }
        return offeredServiceRepository.findByEstablishmentId(establishmentId);
    }

    public OfferedService getServiceById(UUID serviceId) {
        return offeredServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço com ID " + serviceId + " não encontrado."));
    }

    /**
     * Atualiza um serviço existente.
     */
    public OfferedService updateService(UUID serviceId, OfferedServiceRequestDTO requestDTO) {
        OfferedService existingService = getServiceById(serviceId); // Reutiliza o método que já lança exceção se não encontrar

        existingService.setName(requestDTO.name());
        existingService.setDescription(requestDTO.description());
        existingService.setPriceCents(requestDTO.priceCents());
        existingService.setDurationMinutes(requestDTO.duration());

        return offeredServiceRepository.save(existingService);
    }

    /**
     * Deleta um serviço pelo seu ID.
     */
    public void deleteService(UUID serviceId) {
        if (!offeredServiceRepository.existsById(serviceId)) {
            throw new EntityNotFoundException("Serviço com ID " + serviceId + " não encontrado.");
        }
        offeredServiceRepository.deleteById(serviceId);
    }
}
