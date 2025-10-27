package com.school.portal.timetable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    Optional<Timetable> findByProgramAndSemesterAndWeekStart(String program, String semester, LocalDate weekStart);
}
