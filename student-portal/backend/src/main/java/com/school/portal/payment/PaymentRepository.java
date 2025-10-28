package com.school.portal.payment;

import com.school.portal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStudentOrderByCreatedAtDesc(User student);
    List<Payment> findAllByOrderByCreatedAtDesc();
}
