package com.school.portal.payment.dto;

import com.school.portal.payment.PaymentStatus;

import java.time.Instant;

public record PaymentDto(Long id,
                         Long studentId,
                         Long requestId,
                         Long amountCents,
                         String currency,
                         PaymentStatus status,
                         String providerRef,
                         Instant createdAt,
                         Instant updatedAt) {}
