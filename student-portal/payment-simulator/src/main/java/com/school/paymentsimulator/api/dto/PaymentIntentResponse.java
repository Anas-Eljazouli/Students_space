package com.school.paymentsimulator.api.dto;

public record PaymentIntentResponse(Long paymentId, String clientSecret, String status) {
}
