package com.school.portal.absence.dto;

import java.time.Instant;
import java.time.LocalDate;

public record StudentAbsenceDto(Long id,
                                Long studentId,
                                String studentName,
                                String moduleCode,
                                String moduleTitle,
                                String session,
                                LocalDate lessonDate,
                                String reason,
                                Instant createdAt) {
}
