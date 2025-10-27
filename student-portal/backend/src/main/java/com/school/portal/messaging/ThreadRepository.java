package com.school.portal.messaging;

import com.school.portal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadRepository extends JpaRepository<ThreadEntity, Long> {
    List<ThreadEntity> findAllByCreatedBy(User createdBy);
}
