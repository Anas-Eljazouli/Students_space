package com.school.portal.timetable.dto;

import java.time.LocalDate;

public record TimetableDto(Long id,
                           String program,
                           String semester,
                           LocalDate weekStart,
                           String dataJson) {}
