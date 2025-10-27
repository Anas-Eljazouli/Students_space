package com.school.portal.user.dto;

import com.school.portal.common.RoleType;

import java.time.Instant;

public record UserDto(
        Long id,
        String email,
        String fullName,
        RoleType role,
        Instant createdAt
) {}
