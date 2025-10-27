package com.school.portal.timetable.mapper;

import com.school.portal.timetable.Timetable;
import com.school.portal.timetable.dto.TimetableDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimetableMapper {
    TimetableDto toDto(Timetable entity);
}
