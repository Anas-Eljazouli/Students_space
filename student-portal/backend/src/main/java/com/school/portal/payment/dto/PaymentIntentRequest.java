package com.school.portal.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PaymentIntentRequest(@Min(1) Long amountCents,
                                   @NotBlank String currency,
                                   @NotBlank String purpose,
                                   Long requestId) {}
