package com.school.portal.payment.dto;

public record PaymentIntentResponse(Long paymentId, String clientSecret, String status) {}
