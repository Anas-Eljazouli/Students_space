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

    private final TimetableRepository timetableRepository;
    private final TimetableMapper timetableMapper;

    public TimetableDto findCurrentForUser(User user, LocalDate weekStart) {
        return timetableRepository.findAll().stream()
                .filter(t -> t.getWeekStart().equals(weekStart))
                .findFirst()
                .map(timetableMapper::toDto)
                .orElseGet(() -> timetableRepository.findAll().stream().findFirst().map(timetableMapper::toDto).orElse(null));
    }
}
