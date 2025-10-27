package com.school.portal.messaging.dto;

import java.time.Instant;

public record ThreadDto(Long id,
                        String subject,
                        Instant lastMessageAt) {}
