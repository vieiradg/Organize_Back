package com.organize.repository;

import com.organize.model.Customer;
import com.organize.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByUser(User user);
}