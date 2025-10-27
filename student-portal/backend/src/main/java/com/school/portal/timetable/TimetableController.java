package com.school.portal.timetable;

import com.school.portal.timetable.dto.TimetableDto;
import com.school.portal.user.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;
    private final CurrentUserService currentUserService;

    @GetMapping("/my")
    public ResponseEntity<TimetableDto> myTimetable(@AuthenticationPrincipal UserDetails principal,
                                                    @RequestParam(value = "week", required = false)
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        var user = currentUserService.requireUser(principal);
        LocalDate targetWeek = weekStart != null ? weekStart : LocalDate.now();
        return ResponseEntity.ok(timetableService.findCurrentForUser(user, targetWeek));
    }
}
