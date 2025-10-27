package com.school.paymentsimulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class PaymentSimulatorService {

    private final RestTemplate restTemplate;
    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, PaymentIntent> storage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Value("${simulator.webhook-url}")
    private String webhookUrl;

    @Value("${simulator.confirmation-latency}")
    private Duration latency;

    public PaymentIntent createIntent(Long amountCents, String currency, String purpose) {
        long id = sequence.getAndIncrement();
        PaymentIntent intent = PaymentIntent.builder()
                .id(id)
                .amountCents(amountCents)
                .currency(currency)
                .purpose(purpose)
                .status("PENDING")
                .clientSecret("client_secret_" + id)
                .createdAt(Instant.now())
                .build();
        storage.put(id, intent);
        return intent;
    }

    public PaymentIntent confirm(Long id) {
        PaymentIntent intent = storage.get(id);
        if (intent == null) {
            throw new IllegalArgumentException("Payment intent not found");
        }
        String status = random.nextBoolean() ? "SUCCEEDED" : "FAILED";
        intent.setStatus(status);
        sendWebhook(intent);
        return intent;
    }

    public PaymentIntent refund(Long id) {
        PaymentIntent intent = storage.get(id);
        if (intent == null) {
            throw new IllegalArgumentException("Payment intent not found");
        }
        intent.setStatus("REFUNDED");
        sendWebhook(intent);
        return intent;
    }

    public PaymentIntent get(Long id) {
        return storage.get(id);
    }

    private void sendWebhook(PaymentIntent intent) {
        try {
            Thread.sleep(latency.toMillis());
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> payload = Map.of(
                "paymentId", intent.getId(),
                "providerRef", intent.getClientSecret(),
                "status", intent.getStatus()
        );
        restTemplate.postForLocation(webhookUrl, new HttpEntity<>(payload, headers));
    }
}
