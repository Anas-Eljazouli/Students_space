package com.school.portal.payment.dto;

import com.school.portal.payment.PaymentStatus;

import java.time.Instant;

public record PaymentDto(Long id,
                         Long studentId,
                         String studentName,
                         Long requestId,
                         Long amountCents,
                         String currency,
                         String label,
                         String paymentMethod,
                         PaymentStatus status,
                         String justificationUrl,
                         String justificationName,
                         String justificationMime,
                         String statusNotes,
                         Instant createdAt,
                         Instant updatedAt) {}
