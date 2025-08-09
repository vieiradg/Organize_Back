package com.organize.repository;

import com.organize.model.BeautyService;
import com.organize.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<BeautyService, Long> {
    List<BeautyService> findByUser(User user);
}
