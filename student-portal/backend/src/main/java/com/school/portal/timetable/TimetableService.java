package com.school.portal.timetable;

import com.school.portal.timetable.dto.TimetableDto;
import com.school.portal.timetable.mapper.TimetableMapper;
import com.school.portal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private static final String PERSONAL_SEMESTER = "PERSONAL";

    private final TimetableRepository timetableRepository;
    private final TimetableMapper timetableMapper;

    public TimetableDto findCurrentForUser(User user, LocalDate weekStart) {
        LocalDate monday = weekStart.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        return timetableRepository.findByProgramAndSemesterAndWeekStart(user.getEmail(), PERSONAL_SEMESTER, monday)
                .or(() -> timetableRepository.findAll().stream()
                        .filter(t -> t.getWeekStart().equals(monday))
                        .findFirst())
                .or(() -> timetableRepository.findAll().stream()
                        .findFirst())
                .map(timetableMapper::toDto)
                .orElse(null);
    }
}
