package com.school.portal.grade;

import com.school.portal.grade.dto.GradeDto;
import com.school.portal.grade.mapper.GradeMapper;
import com.school.portal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;

    public List<GradeDto> getGradesForStudent(User student) {
        return gradeRepository.findAllByStudentOrderByPublishedAtDesc(student)
                .stream()
                .map(gradeMapper::toDto)
                .toList();
    }
}
