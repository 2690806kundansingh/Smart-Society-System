package com.smartsociety.complaint.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "complaint_audit_logs", indexes = {
        @Index(name = "idx_audit_complaint", columnList = "complaint_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintAuditLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_id", nullable = false)
    private Long complaintId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "performed_by_role", length = 50)
    private String performedByRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private ComplaintStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 30)
    private ComplaintStatus newStatus;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
