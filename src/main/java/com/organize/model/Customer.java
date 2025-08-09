package com.organize.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
