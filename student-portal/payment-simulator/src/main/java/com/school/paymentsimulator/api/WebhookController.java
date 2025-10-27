package com.school.paymentsimulator.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/pay/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final RestTemplate restTemplate;

    @Value("${simulator.webhook-url}")
    private String webhookUrl;

    @PostMapping("/test")
    public ResponseEntity<Void> triggerTest(@RequestBody Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForLocation(webhookUrl, new HttpEntity<>(payload, headers));
        return ResponseEntity.ok().build();
    }
}
