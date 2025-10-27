package com.school.portal.faq;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "faqs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, columnDefinition = "text")
    private String answer;

    @ElementCollection
    @CollectionTable(name = "faq_tags", joinColumns = @JoinColumn(name = "faq_id"))
    @Column(name = "tag")
    private List<String> tags;
}
