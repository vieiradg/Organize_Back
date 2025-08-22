package com.organize.repository;

import com.organize.model.BeautyService;
import com.organize.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<BeautyService, UUID> {
    List<BeautyService> findByUser(User user);
}

