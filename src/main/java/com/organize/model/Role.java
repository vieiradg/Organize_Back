package com.organize.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_PROFESSIONAL,
    ROLE_CUSTOMER;

    @Override
    public String getAuthority() {
        return name();
    }
}