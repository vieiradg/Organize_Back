package com.organize.service;

import com.organize.dto.CustomerRequestDTO;
import com.organize.model.Customer;
import com.organize.model.User;
import com.organize.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(CustomerRequestDTO requestDTO, User user) {
        Customer customer = new Customer();
        customer.setName(requestDTO.name());
        customer.setPhone(requestDTO.phone());
        customer.setUser(user);
        return customerRepository.save(customer);
    }

    public List<Customer> getCustomersByUser(User user) {
        return customerRepository.findByUser(user);
    }
}
