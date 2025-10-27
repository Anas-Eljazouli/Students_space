package com.school.portal.payment;

import com.school.portal.payment.dto.PaymentIntentRequest;
import com.school.portal.payment.dto.PaymentIntentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentGatewayClient {

    private final RestTemplate restTemplate;

    @Value("${payment.simulator.base-url}")
    private String baseUrl;

    @Value("${payment.simulator.api-key}")
    private String apiKey;

    public PaymentIntentResponse createIntent(PaymentIntentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", apiKey);
        HttpEntity<PaymentIntentRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(baseUrl + "/pay/intents", entity, PaymentIntentResponse.class);
    }
}
