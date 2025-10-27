package com.school.portal.payment;

import com.school.portal.notification.UserNotificationService;
import com.school.portal.payment.dto.PaymentDto;
import com.school.portal.payment.dto.PaymentIntentRequest;
import com.school.portal.payment.dto.PaymentIntentResponse;
import com.school.portal.payment.dto.PaymentWebhookPayload;
import com.school.portal.payment.mapper.PaymentMapper;
import com.school.portal.request.StudentRequest;
import com.school.portal.request.StudentRequestRepository;
import com.school.portal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayClient gatewayClient;
    private final StudentRequestRepository requestRepository;
    private final UserNotificationService notificationService;

    @Transactional
    public PaymentIntentResponse createPaymentIntent(User student, PaymentIntentRequest request) {
        PaymentIntentResponse response = gatewayClient.createIntent(request);
        StudentRequest linkedRequest = request.requestId() != null ? requestRepository.findById(request.requestId()).orElse(null) : null;
        if (linkedRequest != null && !linkedRequest.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        Payment payment = Payment.builder()
                .student(student)
                .request(linkedRequest)
                .amountCents(request.amountCents())
                .currency(request.currency())
                .status(PaymentStatus.PENDING)
                .providerRef(response.clientSecret())
                .build();
        paymentRepository.save(payment);
        notificationService.notifyUser(student, "PAYMENT_CREATED", paymentMapper.toDto(payment));
        return new PaymentIntentResponse(payment.getId(), response.clientSecret(), payment.getStatus().name());
    }

    public PaymentDto findById(User user, Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        if (!payment.getStudent().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        return paymentMapper.toDto(payment);
    }

    public List<PaymentDto> listUserPayments(User user) {
        return paymentRepository.findAllByStudentOrderByCreatedAtDesc(user).stream().map(paymentMapper::toDto).toList();
    }

    @Transactional
    public void handleWebhook(PaymentWebhookPayload payload) {
        Payment payment = paymentRepository.findById(payload.paymentId()).orElseThrow();
        payment.setStatus(payload.status());
        payment.setProviderRef(payload.providerRef());
        paymentRepository.save(payment);
        notificationService.notifyUser(payment.getStudent(), "PAYMENT_STATUS", paymentMapper.toDto(payment));
    }
}
