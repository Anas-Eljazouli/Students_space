package com.school.paymentsimulator.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PaymentIntentRequest(@Min(1) Long amountCents,
                                   @NotBlank String currency,
                                   @NotBlank String purpose) {
}
