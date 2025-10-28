package com.school.portal.professor.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProfessorAbsenceRequest(@NotNull Long studentId,
                                      @NotNull String moduleCode,
                                      @NotNull LocalDate lessonDate,
                                      String reason) {
}
