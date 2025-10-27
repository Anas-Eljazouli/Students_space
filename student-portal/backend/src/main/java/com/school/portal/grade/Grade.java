package com.school.portal.grade;

import com.school.portal.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "grades")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

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
    private Double grade;

    private Instant publishedAt;
}
