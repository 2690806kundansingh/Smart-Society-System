package com.smartsociety.complaint.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "complaint_audit_logs", indexes = {
        @Index(name = "idx_audit_complaint", columnList = "complaint_id")
})
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

    public ComplaintAuditLog() {}

    public ComplaintAuditLog(Long id, Long complaintId, String action, Long performedBy,
                             String performedByRole, ComplaintStatus previousStatus,
                             ComplaintStatus newStatus, String notes, Instant createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.action = action;
        this.performedBy = performedBy;
        this.performedByRole = performedByRole;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static ComplaintAuditLogBuilder builder() {
        return new ComplaintAuditLogBuilder();
    }

    public static class ComplaintAuditLogBuilder {
        private Long id;
        private Long complaintId;
        private String action;
        private Long performedBy;
        private String performedByRole;
        private ComplaintStatus previousStatus;
        private ComplaintStatus newStatus;
        private String notes;
        private Instant createdAt;

        public ComplaintAuditLogBuilder id(Long id) { this.id = id; return this; }
        public ComplaintAuditLogBuilder complaintId(Long complaintId) { this.complaintId = complaintId; return this; }
        public ComplaintAuditLogBuilder action(String action) { this.action = action; return this; }
        public ComplaintAuditLogBuilder performedBy(Long performedBy) { this.performedBy = performedBy; return this; }
        public ComplaintAuditLogBuilder performedByRole(String performedByRole) { this.performedByRole = performedByRole; return this; }
        public ComplaintAuditLogBuilder previousStatus(ComplaintStatus previousStatus) { this.previousStatus = previousStatus; return this; }
        public ComplaintAuditLogBuilder newStatus(ComplaintStatus newStatus) { this.newStatus = newStatus; return this; }
        public ComplaintAuditLogBuilder notes(String notes) { this.notes = notes; return this; }
        public ComplaintAuditLogBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public ComplaintAuditLog build() {
            return new ComplaintAuditLog(id, complaintId, action, performedBy, performedByRole, previousStatus, newStatus, notes, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Long getPerformedBy() { return performedBy; }
    public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }

    public String getPerformedByRole() { return performedByRole; }
    public void setPerformedByRole(String performedByRole) { this.performedByRole = performedByRole; }

    public ComplaintStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(ComplaintStatus previousStatus) { this.previousStatus = previousStatus; }

    public ComplaintStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ComplaintStatus newStatus) { this.newStatus = newStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
