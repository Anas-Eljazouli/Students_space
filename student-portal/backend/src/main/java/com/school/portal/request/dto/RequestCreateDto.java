package com.school.portal.request.dto;

import com.school.portal.request.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestCreateDto(@NotNull RequestType type,
                               @NotBlank String payloadJson) {}
