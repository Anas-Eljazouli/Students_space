package com.school.portal.grade;

import com.school.portal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByStudentOrderByPublishedAtDesc(User student);
    Optional<Grade> findByStudentAndModuleCodeAndSession(User student, String moduleCode, String session);
}
