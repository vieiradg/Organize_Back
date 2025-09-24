package com.organize.dto;

import java.util.Set;
import java.util.UUID;

public record UserInfoDTO(
    UUID id, 
    String name, 
    Set<String> roles
) {}