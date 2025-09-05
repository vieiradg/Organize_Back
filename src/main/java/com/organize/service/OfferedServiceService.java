package com.organize.service;

import com.organize.dto.ServiceRequestDTO;
import com.organize.model.Establishment;
import com.organize.model.OfferedService;
import com.organize.model.User;
import com.organize.repository.EstablishmentRepository;
import com.organize.repository.ServiceRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferedServiceService {

    private final ServiceRepository serviceRepository;
    private final EstablishmentRepository establishmentRepository;

    public OfferedServiceService(ServiceRepository serviceRepository, EstablishmentRepository establishmentRepository) {
        this.serviceRepository = serviceRepository;
        this.establishmentRepository = establishmentRepository;
    }

    public OfferedService createService(ServiceRequestDTO requestDTO, User user) {
        Establishment establishment = establishmentRepository.findById(requestDTO.establishmentId())
                .orElseThrow(() -> new IllegalArgumentException("Estabelecimento não encontrado"));


        if (!establishment.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Usuario não é o proprietário do estabelecimento.");
        }

        OfferedService service = new OfferedService();
        service.setName(requestDTO.name());
        service.setDescription(requestDTO.description());
        service.setPriceCents(requestDTO.priceCents());
        service.setDurationMinutes(requestDTO.duration());
        service.setEstablishment(establishment);

        return serviceRepository.save(service);
    }

    public List<OfferedService> getServicesByOwner(User owner) {
        return serviceRepository.findServicesByOwner(owner);
    }
}
