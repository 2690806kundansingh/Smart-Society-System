package com.smartsociety.complaint.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_priority", nullable = false, length = 20)
    @Builder.Default
    private Priority defaultPriority = Priority.MEDIUM;

    @Column(name = "default_sla_hours", nullable = false)
    @Builder.Default
    private Integer defaultSlaHours = 12;
}
