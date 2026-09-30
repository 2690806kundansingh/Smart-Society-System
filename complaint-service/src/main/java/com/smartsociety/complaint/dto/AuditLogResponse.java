package com.smartsociety.complaint.dto;

import com.smartsociety.complaint.entity.ComplaintStatus;

import java.io.Serializable;
import java.time.Instant;

public class AuditLogResponse implements Serializable {
    private Long id;
    private Long complaintId;
    private String action;
    private Long performedBy;
    private String performedByRole;
    private ComplaintStatus previousStatus;
    private ComplaintStatus newStatus;
    private String notes;
    private Instant createdAt;

    public AuditLogResponse() {}

    public AuditLogResponse(Long id, Long complaintId, String action, Long performedBy,
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

    public static AuditLogResponseBuilder builder() {
        return new AuditLogResponseBuilder();
    }

    public static class AuditLogResponseBuilder {
        private Long id;
        private Long complaintId;
        private String action;
        private Long performedBy;
        private String performedByRole;
        private ComplaintStatus previousStatus;
        private ComplaintStatus newStatus;
        private String notes;
        private Instant createdAt;

        public AuditLogResponseBuilder id(Long id) { this.id = id; return this; }
        public AuditLogResponseBuilder complaintId(Long complaintId) { this.complaintId = complaintId; return this; }
        public AuditLogResponseBuilder action(String action) { this.action = action; return this; }
        public AuditLogResponseBuilder performedBy(Long performedBy) { this.performedBy = performedBy; return this; }
        public AuditLogResponseBuilder performedByRole(String performedByRole) { this.performedByRole = performedByRole; return this; }
        public AuditLogResponseBuilder previousStatus(ComplaintStatus previousStatus) { this.previousStatus = previousStatus; return this; }
        public AuditLogResponseBuilder newStatus(ComplaintStatus newStatus) { this.newStatus = newStatus; return this; }
        public AuditLogResponseBuilder notes(String notes) { this.notes = notes; return this; }
        public AuditLogResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AuditLogResponse build() {
            return new AuditLogResponse(id, complaintId, action, performedBy, performedByRole, previousStatus, newStatus, notes, createdAt);
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
