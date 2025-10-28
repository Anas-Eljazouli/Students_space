package com.school.portal.absence;

import com.school.portal.absence.dto.StudentAbsenceDto;
import com.school.portal.user.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/absences")
@RequiredArgsConstructor
public class StudentAbsenceController {

    private final StudentAbsenceService absenceService;
    private final CurrentUserService currentUserService;

    @GetMapping("/my")
    public ResponseEntity<List<StudentAbsenceDto>> myAbsences(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(absenceService.listForStudent(user));
    }
}
