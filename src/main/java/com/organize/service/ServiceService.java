package com.organize.service;

import com.organize.dto.ServiceRequestDTO;
import com.organize.model.BeautyService;
import com.organize.model.User;
import com.organize.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public BeautyService createService(ServiceRequestDTO requestDTO, User user) {
        BeautyService service = new BeautyService();
        service.setName(requestDTO.name());
        service.setDescription(requestDTO.description());
        service.setPrice(requestDTO.price());
        service.setDuration(requestDTO.duration());
        service.setUser(user);
        return serviceRepository.save(service);
    }

    public List<BeautyService> getServicesByUser(User user) {
        return serviceRepository.findByUser(user);
    }
}
