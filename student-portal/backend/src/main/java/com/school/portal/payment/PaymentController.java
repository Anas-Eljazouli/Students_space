package com.school.portal.payment;

import com.school.portal.payment.dto.PaymentDto;
import com.school.portal.payment.dto.PaymentIntentRequest;
import com.school.portal.payment.dto.PaymentIntentResponse;
import com.school.portal.payment.dto.PaymentWebhookPayload;
import com.school.portal.user.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;

    @PostMapping("/intent")
    public ResponseEntity<PaymentIntentResponse> intent(@AuthenticationPrincipal UserDetails principal,
                                                        @RequestBody @Valid PaymentIntentRequest request) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.createPaymentIntent(user, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> get(@AuthenticationPrincipal UserDetails principal,
                                          @PathVariable Long id) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.findById(user, id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentDto>> myPayments(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.listUserPayments(user));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody PaymentWebhookPayload payload) {
        paymentService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
