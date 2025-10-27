package com.school.portal.payment.dto;

import com.school.portal.payment.PaymentStatus;

public record PaymentWebhookPayload(Long paymentId,
                                    String providerRef,
                                    PaymentStatus status) {}
