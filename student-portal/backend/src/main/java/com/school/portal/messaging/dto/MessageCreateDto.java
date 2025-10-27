package com.school.portal.messaging.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageCreateDto(@NotBlank String content) {}
