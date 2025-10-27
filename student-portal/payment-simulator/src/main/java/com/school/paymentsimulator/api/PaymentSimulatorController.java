package com.school.paymentsimulator.api;

import com.school.paymentsimulator.api.dto.PaymentIntentRequest;
import com.school.paymentsimulator.api.dto.PaymentIntentResponse;
import com.school.paymentsimulator.service.PaymentIntent;
import com.school.paymentsimulator.service.PaymentSimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PaymentSimulatorController {

    private final PaymentSimulatorService service;

    @PostMapping("/intents")
    public ResponseEntity<PaymentIntentResponse> create(@RequestBody @Valid PaymentIntentRequest request) {
        PaymentIntent intent = service.createIntent(request.amountCents(), request.currency(), request.purpose());
        return ResponseEntity.ok(new PaymentIntentResponse(intent.getId(), intent.getClientSecret(), intent.getStatus()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentIntentResponse> confirm(@RequestParam Long paymentId) {
        PaymentIntent intent = service.confirm(paymentId);
        return ResponseEntity.ok(new PaymentIntentResponse(intent.getId(), intent.getClientSecret(), intent.getStatus()));
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentIntentResponse> refund(@RequestParam Long paymentId) {
        PaymentIntent intent = service.refund(paymentId);
        return ResponseEntity.ok(new PaymentIntentResponse(intent.getId(), intent.getClientSecret(), intent.getStatus()));
    }

    @GetMapping("/intents/{id}")
    public ResponseEntity<PaymentIntentResponse> get(@PathVariable Long id) {
        PaymentIntent intent = service.get(id);
        if (intent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new PaymentIntentResponse(intent.getId(), intent.getClientSecret(), intent.getStatus()));
    }
}
