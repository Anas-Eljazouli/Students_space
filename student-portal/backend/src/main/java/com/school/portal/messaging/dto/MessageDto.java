package com.school.portal.messaging.dto;

import java.time.Instant;

public record MessageDto(Long id,
                         Long senderId,
                         String content,
                         Instant createdAt) {}
