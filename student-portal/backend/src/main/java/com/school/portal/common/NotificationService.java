package com.school.portal.common;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(Long userId, String type, Object payload) {
        messagingTemplate.convertAndSend("/topic/users/" + userId, new NotificationPayload(type, payload));
    }

    public record NotificationPayload(String type, Object payload) {
    }
}
