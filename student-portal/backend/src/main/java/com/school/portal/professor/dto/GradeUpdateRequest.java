package com.school.portal.professor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GradeUpdateRequest(@NotNull Long studentId,
                                 @NotNull String moduleCode,
                                 @Min(0) @Max(20) Double grade,
                                 String session) {
}
