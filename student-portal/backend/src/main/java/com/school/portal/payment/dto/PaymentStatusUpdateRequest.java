package com.school.portal.payment.dto;

import com.school.portal.payment.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentStatusUpdateRequest(@NotNull PaymentStatus status,
                                         String notes) {
}
