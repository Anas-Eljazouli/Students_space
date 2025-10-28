package com.school.portal.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PaymentSubmissionForm {

    @NotNull
    @Min(1)
    private Long amountCents;

    @NotBlank
    private String currency = "EUR";

    @NotBlank
    private String label;

    @NotBlank
    private String paymentMethod;

    private Long requestId;

    private MultipartFile justification;
}
