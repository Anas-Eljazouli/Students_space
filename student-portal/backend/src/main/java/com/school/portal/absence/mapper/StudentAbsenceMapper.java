package com.school.portal.absence.mapper;

import com.school.portal.absence.StudentAbsence;
import com.school.portal.absence.dto.StudentAbsenceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentAbsenceMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    StudentAbsenceDto toDto(StudentAbsence absence);
}
