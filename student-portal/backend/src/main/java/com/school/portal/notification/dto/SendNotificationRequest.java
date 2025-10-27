package com.school.portal.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendNotificationRequest(
        @NotNull Long studentId,
        @NotBlank String subject,
        @NotBlank String message
) {
}
