package com.school.portal.timetable;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "timetables")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String program;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private LocalDate weekStart;

    @Column(columnDefinition = "jsonb")
    private String dataJson;
}
