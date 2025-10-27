package com.school.paymentsimulator.service;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PaymentIntent {
    private Long id;
    private Long amountCents;
    private String currency;
    private String status;
    private String clientSecret;
    private Instant createdAt;
    private String purpose;
}
