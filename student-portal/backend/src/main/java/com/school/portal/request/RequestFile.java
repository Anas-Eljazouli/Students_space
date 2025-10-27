package com.school.portal.request;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "request_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private StudentRequest request;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String mime;

    @Column(nullable = false)
    private String url;

    @CreationTimestamp
    private Instant uploadedAt;
}
