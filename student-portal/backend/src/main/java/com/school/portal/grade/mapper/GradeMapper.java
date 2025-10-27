package com.school.portal.grade.mapper;

import com.school.portal.grade.Grade;
import com.school.portal.grade.dto.GradeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    GradeDto toDto(Grade entity);
}
