package com.school.portal.grade.dto;

import java.time.Instant;

public record GradeDto(Long id,
                       String moduleCode,
                       String moduleTitle,
                       String session,
                       Double grade,
                       Instant publishedAt) {}
