package com.school.portal.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.portal.common.NotificationService;
import com.school.portal.notification.dto.NotificationDto;
import com.school.portal.user.User;
import com.school.portal.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserNotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void notifyUser(User user, String type, Object payload) {
        storeAndDispatch(user, type, payload);
    }

    @Transactional
    public void notifyUser(Long userId, String type, Object payload) {
        User user = userRepository.findById(userId).orElseThrow();
        storeAndDispatch(user, type, payload);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> listForUser(User user) {
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(entity -> new NotificationDto(
                        entity.getId(),
                        entity.getType(),
                        readPayload(entity.getPayloadJson()),
                        entity.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void clearForUser(User user) {
        notificationRepository.deleteAllByUser(user);
    }

    private void storeAndDispatch(User user, String type, Object payload) {
        String payloadJson = writePayload(payload);
        notificationRepository.save(UserNotification.builder()
                .user(user)
                .type(type)
                .payloadJson(payloadJson)
                .build());
        notificationService.notifyUser(user.getId(), type, payload);
    }

    private String writePayload(Object payload) {
        if (payload == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize notification payload", exception);
        }
    }

    private Object readPayload(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(payloadJson, Object.class);
        } catch (JsonProcessingException exception) {
            // fallback to raw JSON string if parsing fails
            return payloadJson;
        }
    }
}
