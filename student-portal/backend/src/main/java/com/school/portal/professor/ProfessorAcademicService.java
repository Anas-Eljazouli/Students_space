package com.school.portal.professor;

import com.school.portal.absence.StudentAbsence;
import com.school.portal.absence.StudentAbsenceRepository;
import com.school.portal.common.RoleType;
import com.school.portal.grade.Grade;
import com.school.portal.grade.GradeRepository;
import com.school.portal.grade.dto.GradeDto;
import com.school.portal.grade.mapper.GradeMapper;
import com.school.portal.professor.dto.*;
import com.school.portal.user.User;
import com.school.portal.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfessorAcademicService {

    private static final String DEFAULT_SESSION = "2024-Fall";

    private static final Map<String, ModuleInfo> SUPERVISED_MODULES = Map.of(
            "NET-FOR", new ModuleInfo("NET-FOR", "Network Forensics"),
            "DATA-VIS", new ModuleInfo("DATA-VIS", "Data Visualization Studio")
    );

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;
    private final StudentAbsenceRepository absenceRepository;

    @Transactional(readOnly = true)
    public List<ProfessorStudentDto> getRoster() {
        List<User> students = userRepository.findAllByRole(RoleType.STUDENT);
        List<ProfessorStudentDto> roster = new ArrayList<>();
        for (User student : students) {
            List<StudentAbsence> absences = absenceRepository.findAllByStudentOrderByLessonDateDesc(student);
            Map<String, List<ModuleAbsenceDto>> absencesByModule = mapAbsences(absences);
            List<StudentModuleProgressDto> modules = SUPERVISED_MODULES.values().stream()
                    .map(module -> buildModuleProgress(student, module, absencesByModule.getOrDefault(module.code(), List.of())))
                    .toList();
            roster.add(new ProfessorStudentDto(
                    student.getId(),
                    student.getFullName(),
                    student.getEmail(),
                    modules
            ));
        }
        return roster;
    }

    @Transactional
    public GradeDto updateGrade(User professor, GradeUpdateRequest request) {
        ModuleInfo module = requireModule(request.moduleCode());
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        String session = request.session() != null ? request.session() : DEFAULT_SESSION;
        Grade grade = gradeRepository.findByStudentAndModuleCodeAndSession(student, module.code(), session)
                .orElseGet(() -> Grade.builder()
                        .student(student)
                        .moduleCode(module.code())
                        .moduleTitle(module.title())
                        .session(session)
                        .build());
        grade.setGrade(request.grade());
        grade.setModuleTitle(module.title());
        grade.setPublishedAt(Instant.now());
        gradeRepository.save(grade);
        return gradeMapper.toDto(grade);
    }

    @Transactional
    public ModuleAbsenceDto recordAbsence(User professor, ProfessorAbsenceRequest request) {
        ModuleInfo module = requireModule(request.moduleCode());
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        LocalDate lessonDate = request.lessonDate();
        StudentAbsence absence = absenceRepository.findByStudentAndModuleCodeAndLessonDate(student, module.code(), lessonDate)
                .orElseGet(() -> StudentAbsence.builder()
                        .student(student)
                        .moduleCode(module.code())
                        .moduleTitle(module.title())
                        .session(DEFAULT_SESSION)
                        .lessonDate(lessonDate)
                        .build());
        absence.setReason(request.reason());
        absenceRepository.save(absence);
        return new ModuleAbsenceDto(absence.getId(), absence.getLessonDate(), absence.getReason());
    }

    @Transactional
    public void removeAbsence(User professor, Long absenceId) {
        absenceRepository.deleteById(absenceId);
    }

    private ModuleInfo requireModule(String code) {
        ModuleInfo module = SUPERVISED_MODULES.get(code);
        if (module == null) {
            throw new IllegalArgumentException("Module non pris en charge");
        }
        return module;
    }

    private Map<String, List<ModuleAbsenceDto>> mapAbsences(List<StudentAbsence> absences) {
        Map<String, List<ModuleAbsenceDto>> byModule = new HashMap<>();
        for (StudentAbsence absence : absences) {
            byModule.computeIfAbsent(absence.getModuleCode(), key -> new ArrayList<>())
                    .add(new ModuleAbsenceDto(absence.getId(), absence.getLessonDate(), absence.getReason()));
        }
        byModule.values().forEach(list -> list.sort(Comparator.comparing(ModuleAbsenceDto::lessonDate).reversed()));
        return byModule;
    }

    private StudentModuleProgressDto buildModuleProgress(User student, ModuleInfo module, List<ModuleAbsenceDto> absences) {
        Grade grade = gradeRepository.findByStudentAndModuleCodeAndSession(student, module.code(), DEFAULT_SESSION).orElse(null);
        Double gradeValue = grade != null ? grade.getGrade() : null;
        return new StudentModuleProgressDto(module.code(), module.title(), gradeValue, absences);
    }

    private record ModuleInfo(String code, String title) {}
}
