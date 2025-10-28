package com.school.portal.absence;

import com.school.portal.absence.dto.StudentAbsenceDto;
import com.school.portal.absence.mapper.StudentAbsenceMapper;
import com.school.portal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAbsenceService {

    private final StudentAbsenceRepository absenceRepository;
    private final StudentAbsenceMapper absenceMapper;

    @Transactional(readOnly = true)
    public List<StudentAbsenceDto> listForStudent(User student) {
        return absenceRepository.findAllByStudentOrderByLessonDateDesc(student).stream()
                .map(absenceMapper::toDto)
                .toList();
    }
}
