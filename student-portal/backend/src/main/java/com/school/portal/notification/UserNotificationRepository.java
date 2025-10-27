package com.school.portal.notification;

import com.school.portal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findAllByUserOrderByCreatedAtDesc(User user);

    void deleteAllByUser(User user);
}
