package com.school.portal.grade;

import com.school.portal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByStudentOrderByPublishedAtDesc(User student);
}
