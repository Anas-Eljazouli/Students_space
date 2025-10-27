package com.school.portal.notification;

import com.school.portal.notification.dto.NotificationDto;
import com.school.portal.user.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService notificationService;
    private final CurrentUserService currentUserService;

    @GetMapping("/my")
    public ResponseEntity<List<NotificationDto>> myNotifications(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(notificationService.listForUser(user));
    }

    @DeleteMapping("/my")
    public ResponseEntity<Void> clear(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        notificationService.clearForUser(user);
        return ResponseEntity.noContent().build();
    }
}
