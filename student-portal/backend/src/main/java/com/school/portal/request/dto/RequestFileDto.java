package com.school.portal.request.dto;

import java.time.Instant;

public record RequestFileDto(Long id,
                             String filename,
                             String mime,
                             String url,
                             Instant uploadedAt) {}
