package com.school.portal.messaging.dto;

import jakarta.validation.constraints.NotBlank;

public record ThreadCreateDto(@NotBlank String subject,
                              @NotBlank String initialMessage) {}
