package com.school.portal.messaging;

import com.school.portal.notification.UserNotificationService;
import com.school.portal.messaging.dto.MessageCreateDto;
import com.school.portal.messaging.dto.MessageDto;
import com.school.portal.messaging.dto.ThreadCreateDto;
import com.school.portal.messaging.dto.ThreadDto;
import com.school.portal.messaging.mapper.ThreadMapper;
import com.school.portal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final ThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final ThreadMapper threadMapper;
    private final UserNotificationService notificationService;

    public List<ThreadDto> myThreads(User user) {
        return threadRepository.findAllByCreatedBy(user).stream().map(threadMapper::toDto).toList();
    }

    @Transactional
    public ThreadDto createThread(User user, ThreadCreateDto dto) {
        ThreadEntity thread = threadRepository.save(ThreadEntity.builder()
                .subject(dto.subject())
                .createdBy(user)
                .build());
        MessageEntity message = messageRepository.save(MessageEntity.builder()
                .thread(thread)
                .sender(user)
                .content(dto.initialMessage())
                .build());
        thread.setLastMessageAt(message.getCreatedAt());
        threadRepository.save(thread);
        notifyParticipants(thread, message, "THREAD_CREATED");
        return threadMapper.toDto(thread);
    }

    public List<MessageDto> getMessages(ThreadEntity thread) {
        return messageRepository.findAllByThreadOrderByCreatedAtAsc(thread)
                .stream().map(threadMapper::toDto).toList();
    }

    @Transactional
    public MessageDto sendMessage(User sender, Long threadId, MessageCreateDto dto) {
        ThreadEntity thread = threadRepository.findById(threadId).orElseThrow();
        MessageEntity message = messageRepository.save(MessageEntity.builder()
                .thread(thread)
                .sender(sender)
                .content(dto.content())
                .build());
        thread.setLastMessageAt(message.getCreatedAt());
        threadRepository.save(thread);
        notifyParticipants(thread, message, "MESSAGE_CREATED");
        return threadMapper.toDto(message);
    }

    public ThreadEntity requireThread(Long id) {
        return threadRepository.findById(id).orElseThrow();
    }

    private void notifyParticipants(ThreadEntity thread, MessageEntity message, String type) {
        notificationService.notifyUser(thread.getCreatedBy(), type, threadMapper.toDto(message));
    }
}
