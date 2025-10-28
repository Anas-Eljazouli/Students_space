package com.school.portal.payment;

import com.school.portal.payment.dto.PaymentDto;
import com.school.portal.payment.dto.PaymentStatusUpdateRequest;
import com.school.portal.payment.dto.PaymentSubmissionForm;
import com.school.portal.user.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PaymentDto> submit(@AuthenticationPrincipal UserDetails principal,
                                             @Valid PaymentSubmissionForm form) throws IOException {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.submitPayment(user, form));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> get(@AuthenticationPrincipal UserDetails principal,
                                          @PathVariable Long id) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.findById(user, id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentDto>> myPayments(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.listUserPayments(user));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> all(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.listAll(user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentDto> updateStatus(@AuthenticationPrincipal UserDetails principal,
                                                   @PathVariable Long id,
                                                   @RequestBody @Valid PaymentStatusUpdateRequest request) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(paymentService.updateStatus(user, id, request));
    }
}
