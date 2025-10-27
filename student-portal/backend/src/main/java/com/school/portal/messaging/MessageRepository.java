package com.school.portal.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findAllByThreadOrderByCreatedAtAsc(ThreadEntity thread);
}
