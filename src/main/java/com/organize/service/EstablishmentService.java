package com.organize.service;

import com.organize.dto.EstablishmentRequestDTO;
import com.organize.model.Establishment;
import com.organize.model.User;
import com.organize.repository.EstablishmentRepository;
import org.springframework.stereotype.Service;

@Service 
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;

  
    public EstablishmentService(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }


    public Establishment createEstablishment(EstablishmentRequestDTO data, User owner) {
        Establishment newEstablishment = new Establishment();

        newEstablishment.setName(data.name());
        newEstablishment.setAddress(data.address());
        newEstablishment.setContactPhone(data.contactPhone());
        newEstablishment.setOwner(owner); 

        return establishmentRepository.save(newEstablishment);
    }
}