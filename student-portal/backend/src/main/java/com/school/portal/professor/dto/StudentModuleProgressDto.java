package com.school.portal.professor.dto;

import java.util.List;

public record StudentModuleProgressDto(String moduleCode,
                                       String moduleTitle,
                                       Double grade,
                                       List<ModuleAbsenceDto> absences) {
}
