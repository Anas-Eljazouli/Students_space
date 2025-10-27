package com.school.portal.security.dto;

import com.school.portal.user.dto.UserDto;

public record AuthResponse(String accessToken, String refreshToken, UserDto user) {}
