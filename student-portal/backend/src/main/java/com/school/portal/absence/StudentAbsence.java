package com.school.portal.absence;

import com.school.portal.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "student_absences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAbsence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private String moduleCode;

    @Column(nullable = false)
    private String moduleTitle;

    @Column(nullable = false)
    private String session;

    @Column(nullable = false)
    private LocalDate lessonDate;

    @Column
    private String reason;

    @CreationTimestamp
    private Instant createdAt;
}
