package com.school.portal.professor.dto;

import java.time.LocalDate;

public record ModuleAbsenceDto(Long id,
                               LocalDate lessonDate,
                               String reason) {
}
