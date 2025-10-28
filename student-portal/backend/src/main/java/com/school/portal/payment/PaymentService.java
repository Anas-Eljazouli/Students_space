package com.school.portal.payment;

import com.school.portal.common.RoleType;
import com.school.portal.notification.UserNotificationService;
import com.school.portal.payment.dto.PaymentDto;
import com.school.portal.payment.dto.PaymentStatusUpdateRequest;
import com.school.portal.payment.dto.PaymentSubmissionForm;
import com.school.portal.payment.mapper.PaymentMapper;
import com.school.portal.request.StudentRequest;
import com.school.portal.request.StudentRequestRepository;
import com.school.portal.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String DEFAULT_CURRENCY = "EUR";

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StudentRequestRepository requestRepository;
    private final UserNotificationService notificationService;

    @Transactional
    public PaymentDto submitPayment(User student, @Valid PaymentSubmissionForm form) throws IOException {
        StudentRequest linkedRequest = resolveRequest(student, form.getRequestId());

        Payment payment = Payment.builder()
                .student(student)
                .request(linkedRequest)
                .amountCents(form.getAmountCents())
                .currency(form.getCurrency() != null && !form.getCurrency().isBlank() ? form.getCurrency() : DEFAULT_CURRENCY)
                .label(form.getLabel())
                .paymentMethod(form.getPaymentMethod())
                .status(PaymentStatus.PROCESSING)
                .build();

        handleJustification(payment, form.getJustification());

        Payment saved = paymentRepository.save(payment);
        PaymentDto dto = paymentMapper.toDto(saved);
        notificationService.notifyUser(student, "PAYMENT_SUBMITTED", dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public PaymentDto findById(User requester, Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        if (!isOwner(requester, payment) && !isReviewer(requester)) {
            throw new IllegalArgumentException("Access denied");
        }
        return paymentMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> listUserPayments(User student) {
        return paymentRepository.findAllByStudentOrderByCreatedAtDesc(student)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> listAll(User reviewer) {
        if (!isReviewer(reviewer)) {
            throw new IllegalArgumentException("Access denied");
        }
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Transactional
    public PaymentDto updateStatus(User reviewer, Long id, PaymentStatusUpdateRequest request) {
        if (!isReviewer(reviewer)) {
            throw new IllegalArgumentException("Access denied");
        }
        if (request.status() == PaymentStatus.PROCESSING) {
            throw new IllegalArgumentException("Cannot revert a payment to processing");
        }
        Payment payment = paymentRepository.findById(id).orElseThrow();
        payment.setStatus(request.status());
        payment.setStatusNotes(request.notes());
        Payment saved = paymentRepository.save(payment);
        PaymentDto dto = paymentMapper.toDto(saved);
        notificationService.notifyUser(payment.getStudent(), "PAYMENT_STATUS", dto);
        return dto;
    }

    private StudentRequest resolveRequest(User student, Long requestId) {
        if (requestId == null) {
            return null;
        }
        StudentRequest linkedRequest = requestRepository.findById(requestId).orElseThrow();
        if (!linkedRequest.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        return linkedRequest;
    }

    private void handleJustification(Payment payment, MultipartFile justification) throws IOException {
        if (justification == null || justification.isEmpty()) {
            return;
        }
        Path uploads = Path.of("uploads/payments").toAbsolutePath();
        Files.createDirectories(uploads);
        String sanitizedOriginalName = justification.getOriginalFilename() != null ? justification.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_") : "justificatif";
        String storedName = Instant.now().toEpochMilli() + "_" + UUID.randomUUID() + "_" + sanitizedOriginalName;
        Path destination = uploads.resolve(storedName);
        justification.transferTo(destination);
        payment.setJustificationName(justification.getOriginalFilename());
        payment.setJustificationMime(justification.getContentType());
        payment.setJustificationUrl("/uploads/payments/" + storedName);
    }

    private boolean isOwner(User user, Payment payment) {
        return payment.getStudent().getId().equals(user.getId());
    }

    private boolean isReviewer(User user) {
        RoleType role = user.getRole();
        return role == RoleType.ADMIN;
    }
}
