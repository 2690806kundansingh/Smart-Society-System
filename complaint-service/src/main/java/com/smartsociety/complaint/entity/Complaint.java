package com.smartsociety.complaint.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "complaints", indexes = {
        @Index(name = "idx_complaints_soc_status", columnList = "society_id, status"),
        @Index(name = "idx_complaints_sla", columnList = "sla_deadline, sla_breached"),
        @Index(name = "idx_complaints_staff", columnList = "assigned_staff_id, status"),
        @Index(name = "idx_complaints_resident", columnList = "resident_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "society_id", nullable = false)
    private Long societyId;

    @Column(name = "resident_id", nullable = false)
    private Long residentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_details", length = 255)
    private String locationDetails;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(name = "assigned_staff_id")
    private Long assignedStaffId;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolution_photo_url", length = 500)
    private String resolutionPhotoUrl;

    @Column(name = "sla_deadline", nullable = false)
    private Instant slaDeadline;

    @Column(name = "sla_breached", nullable = false)
    @Builder.Default
    private Boolean slaBreached = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean escalated = false;

    /**
     * Optimistic locking version field to prevent race conditions during concurrent staff assignment
     */
    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
