package com.school.portal.messaging;

import com.school.portal.messaging.dto.MessageCreateDto;
import com.school.portal.messaging.dto.MessageDto;
import com.school.portal.messaging.dto.ThreadCreateDto;
import com.school.portal.messaging.dto.ThreadDto;
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
@RequestMapping("/api/threads")
@RequiredArgsConstructor
public class MessagingController {

    private final MessagingService messagingService;
    private final CurrentUserService currentUserService;

    @GetMapping("/my")
    public ResponseEntity<List<ThreadDto>> myThreads(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(messagingService.myThreads(user));
    }

    @PostMapping
    public ResponseEntity<ThreadDto> create(@AuthenticationPrincipal UserDetails principal,
                                            @RequestBody @Valid ThreadCreateDto dto) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(messagingService.createThread(user, dto));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDto>> messages(@AuthenticationPrincipal UserDetails principal,
                                                     @PathVariable Long id) {
        var user = currentUserService.requireUser(principal);
        ThreadEntity thread = messagingService.requireThread(id);
        if (!thread.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        return ResponseEntity.ok(messagingService.getMessages(thread));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageDto> send(@AuthenticationPrincipal UserDetails principal,
                                           @PathVariable Long id,
                                           @RequestBody @Valid MessageCreateDto dto) {
        var user = currentUserService.requireUser(principal);
        ThreadEntity thread = messagingService.requireThread(id);
        if (!thread.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        return ResponseEntity.ok(messagingService.sendMessage(user, id, dto));
    }
}
