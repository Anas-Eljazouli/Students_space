package com.school.portal.request.dto;

import com.school.portal.request.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record RequestUpdateDto(@NotNull RequestStatus status,
                               String payloadJson) {}
