package com.organize.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_PROFESSIONAL;

    @Override
    public String getAuthority() {
        return name();
    }
}
