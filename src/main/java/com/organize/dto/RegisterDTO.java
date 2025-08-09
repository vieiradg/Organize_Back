package com.organize.dto;

import com.organize.model.Role;

import java.util.Set;

public record RegisterDTO(String username, String password, Set<Role> roles) {
}
