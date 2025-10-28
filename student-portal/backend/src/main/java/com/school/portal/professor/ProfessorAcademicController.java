package com.school.portal.professor;

import com.school.portal.grade.dto.GradeDto;
import com.school.portal.professor.dto.*;
import com.school.portal.user.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROFESSOR','ADMIN')")
public class ProfessorAcademicController {

    private final ProfessorAcademicService academicService;
    private final CurrentUserService currentUserService;

    @GetMapping("/students")
    public ResponseEntity<List<ProfessorStudentDto>> students() {
        return ResponseEntity.ok(academicService.getRoster());
    }

    @PostMapping("/grades")
    public ResponseEntity<GradeDto> updateGrade(@AuthenticationPrincipal UserDetails principal,
                                                @RequestBody @Valid GradeUpdateRequest request) {
        var professor = currentUserService.requireUser(principal);
        return ResponseEntity.ok(academicService.updateGrade(professor, request));
    }

    @PostMapping("/absences")
    public ResponseEntity<ModuleAbsenceDto> recordAbsence(@AuthenticationPrincipal UserDetails principal,
                                                          @RequestBody @Valid ProfessorAbsenceRequest request) {
        var professor = currentUserService.requireUser(principal);
        return ResponseEntity.ok(academicService.recordAbsence(professor, request));
    }

    @DeleteMapping("/absences/{id}")
    public ResponseEntity<Void> deleteAbsence(@AuthenticationPrincipal UserDetails principal,
                                              @PathVariable Long id) {
        var professor = currentUserService.requireUser(principal);
        academicService.removeAbsence(professor, id);
        return ResponseEntity.noContent().build();
    }
}
