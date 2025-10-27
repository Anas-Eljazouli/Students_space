package com.school.portal.notification.dto;

import java.time.Instant;

public record NotificationDto(Long id, String type, Object payload, Instant createdAt) {
}
