package com.school.portal.professor.dto;

import java.util.List;

public record ProfessorStudentDto(Long studentId,
                                  String studentName,
                                  String email,
                                  List<StudentModuleProgressDto> modules) {
}
