package com.school.portal.request;

import com.school.portal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRequestRepository extends JpaRepository<StudentRequest, Long> {
    List<StudentRequest> findAllByStudentOrderByCreatedAtDesc(User student);
    List<StudentRequest> findAllByStatusOrderByCreatedAtDesc(RequestStatus status);
}
