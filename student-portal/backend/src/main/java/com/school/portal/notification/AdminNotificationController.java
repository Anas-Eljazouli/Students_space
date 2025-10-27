package com.school.portal.notification;

import com.school.portal.common.RoleType;
import com.school.portal.notification.dto.SendNotificationRequest;
import com.school.portal.user.CurrentUserService;
import com.school.portal.user.UserRepository;
import com.school.portal.user.UserService;
import com.school.portal.user.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final UserNotificationService notificationService;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<List<UserDto>> listStudents() {
        return ResponseEntity.ok(userService.findAllByRole(RoleType.STUDENT));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Void> sendNotification(@AuthenticationPrincipal UserDetails principal,
                                                 @Valid @RequestBody SendNotificationRequest request) {
        var sender = currentUserService.requireUser(principal);
        var student = userRepository.findById(request.studentId()).orElseThrow();
        notificationService.notifyUser(
                student,
                "ADMIN_MESSAGE",
                Map.of(
                        "subject", request.subject(),
                        "message", request.message(),
                        "sender", sender.getFullName()
                )
        );
        return ResponseEntity.ok().build();
    }
}
