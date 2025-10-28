package com.school.portal.payment;

import com.school.portal.request.StudentRequest;
import com.school.portal.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private StudentRequest request;

    @Column(nullable = false)
    private Long amountCents;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String label;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "justification_url")
    private String justificationUrl;

    @Column(name = "justification_name")
    private String justificationName;

    @Column(name = "justification_mime")
    private String justificationMime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "status_notes")
    private String statusNotes;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
