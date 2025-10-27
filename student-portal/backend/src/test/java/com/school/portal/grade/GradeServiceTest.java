package com.school.portal.grade;

import com.school.portal.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class GradeServiceTest extends AbstractIntegrationTest {

    @Autowired
    GradeService gradeService;

    @Autowired
    GradeRepository gradeRepository;

    @Test
    void shouldLoadDemoGrades() {
        var user = gradeRepository.findAll().stream()
                .findFirst()
                .map(Grade::getStudent)
                .orElseThrow();
        var grades = gradeService.getGradesForStudent(user);
        assertThat(grades).isNotEmpty();
    }
}
