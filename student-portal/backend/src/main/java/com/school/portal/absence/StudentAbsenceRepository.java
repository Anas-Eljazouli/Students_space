package com.school.portal.absence;

import com.school.portal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentAbsenceRepository extends JpaRepository<StudentAbsence, Long> {
    List<StudentAbsence> findAllByStudentOrderByLessonDateDesc(User student);
    Optional<StudentAbsence> findByStudentAndModuleCodeAndLessonDate(User student, String moduleCode, LocalDate lessonDate);
}
