package com.school.portal.request.dto;

import com.school.portal.request.RequestStatus;
import com.school.portal.request.RequestType;

import java.time.Instant;
import java.util.List;

public record StudentRequestDto(Long id,
                                Long studentId,
                                RequestType type,
                                RequestStatus status,
                                String payloadJson,
                                Instant createdAt,
                                Instant updatedAt,
                                List<RequestFileDto> files) {}
