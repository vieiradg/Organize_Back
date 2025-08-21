package com.organize.dto;

import com.organize.model.Role;

import java.util.Set;

public record RegisterDTO(String name, String email, String phone, String password, Set<Role> roles) {
}
