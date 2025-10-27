package com.school.portal.request.mapper;

import com.school.portal.request.RequestFile;
import com.school.portal.request.StudentRequest;
import com.school.portal.request.dto.RequestFileDto;
import com.school.portal.request.dto.StudentRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "files", source = "files")
    @Mapping(target = "studentId", source = "student.id")
    StudentRequestDto toDto(StudentRequest request);

    RequestFileDto toDto(RequestFile file);
}
