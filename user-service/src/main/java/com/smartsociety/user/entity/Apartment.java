package com.smartsociety.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "apartments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"society_id", "block_name", "flat_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "block_name", nullable = false, length = 50)
    private String blockName;

    @Column(name = "flat_number", nullable = false, length = 50)
    private String flatNumber;

    @Column(name = "society_id", nullable = false)
    private Long societyId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
