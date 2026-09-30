package com.smartsociety.complaint.dto;

import com.smartsociety.complaint.entity.Category;
import com.smartsociety.complaint.entity.ComplaintStatus;
import com.smartsociety.complaint.entity.Priority;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class ComplaintResponse implements Serializable {

    private Long id;
    private Long societyId;
    private Long residentId;
    private Category category;
    private String title;
    private String description;
    private String locationDetails;
    private String photoUrl;
    private Priority priority;
    private ComplaintStatus status;
    private Long assignedStaffId;
    private String resolutionNotes;
    private String resolutionPhotoUrl;
    private Instant slaDeadline;
    private Boolean slaBreached;
    private Boolean escalated;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private FeedbackResponse feedback;
    private List<AuditLogResponse> auditLogs;

    public ComplaintResponse() {}

    public ComplaintResponse(Long id, Long societyId, Long residentId, Category category,
                             String title, String description, String locationDetails,
                             String photoUrl, Priority priority, ComplaintStatus status,
                             Long assignedStaffId, String resolutionNotes, String resolutionPhotoUrl,
                             Instant slaDeadline, Boolean slaBreached, Boolean escalated,
                             Long version, Instant createdAt, Instant updatedAt, Instant resolvedAt,
                             FeedbackResponse feedback, List<AuditLogResponse> auditLogs) {
        this.id = id;
        this.societyId = societyId;
        this.residentId = residentId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.locationDetails = locationDetails;
        this.photoUrl = photoUrl;
        this.priority = priority;
        this.status = status;
        this.assignedStaffId = assignedStaffId;
        this.resolutionNotes = resolutionNotes;
        this.resolutionPhotoUrl = resolutionPhotoUrl;
        this.slaDeadline = slaDeadline;
        this.slaBreached = slaBreached;
        this.escalated = escalated;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
        this.feedback = feedback;
        this.auditLogs = auditLogs;
    }

    public static ComplaintResponseBuilder builder() {
        return new ComplaintResponseBuilder();
    }

    public static class ComplaintResponseBuilder {
        private Long id;
        private Long societyId;
        private Long residentId;
        private Category category;
        private String title;
        private String description;
        private String locationDetails;
        private String photoUrl;
        private Priority priority;
        private ComplaintStatus status;
        private Long assignedStaffId;
        private String resolutionNotes;
        private String resolutionPhotoUrl;
        private Instant slaDeadline;
        private Boolean slaBreached;
        private Boolean escalated;
        private Long version;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant resolvedAt;
        private FeedbackResponse feedback;
        private List<AuditLogResponse> auditLogs;

        public ComplaintResponseBuilder id(Long id) { this.id = id; return this; }
        public ComplaintResponseBuilder societyId(Long societyId) { this.societyId = societyId; return this; }
        public ComplaintResponseBuilder residentId(Long residentId) { this.residentId = residentId; return this; }
        public ComplaintResponseBuilder category(Category category) { this.category = category; return this; }
        public ComplaintResponseBuilder title(String title) { this.title = title; return this; }
        public ComplaintResponseBuilder description(String description) { this.description = description; return this; }
        public ComplaintResponseBuilder locationDetails(String locationDetails) { this.locationDetails = locationDetails; return this; }
        public ComplaintResponseBuilder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }
        public ComplaintResponseBuilder priority(Priority priority) { this.priority = priority; return this; }
        public ComplaintResponseBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintResponseBuilder assignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public ComplaintResponseBuilder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public ComplaintResponseBuilder resolutionPhotoUrl(String resolutionPhotoUrl) { this.resolutionPhotoUrl = resolutionPhotoUrl; return this; }
        public ComplaintResponseBuilder slaDeadline(Instant slaDeadline) { this.slaDeadline = slaDeadline; return this; }
        public ComplaintResponseBuilder slaBreached(Boolean slaBreached) { this.slaBreached = slaBreached; return this; }
        public ComplaintResponseBuilder escalated(Boolean escalated) { this.escalated = escalated; return this; }
        public ComplaintResponseBuilder version(Long version) { this.version = version; return this; }
        public ComplaintResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintResponseBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public ComplaintResponseBuilder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public ComplaintResponseBuilder feedback(FeedbackResponse feedback) { this.feedback = feedback; return this; }
        public ComplaintResponseBuilder auditLogs(List<AuditLogResponse> auditLogs) { this.auditLogs = auditLogs; return this; }

        public ComplaintResponse build() {
            return new ComplaintResponse(id, societyId, residentId, category, title, description, locationDetails,
                    photoUrl, priority, status, assignedStaffId, resolutionNotes, resolutionPhotoUrl,
                    slaDeadline, slaBreached, escalated, version, createdAt, updatedAt, resolvedAt,
                    feedback, auditLogs);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSocietyId() { return societyId; }
    public void setSocietyId(Long societyId) { this.societyId = societyId; }

    public Long getResidentId() { return residentId; }
    public void setResidentId(Long residentId) { this.residentId = residentId; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationDetails() { return locationDetails; }
    public void setLocationDetails(String locationDetails) { this.locationDetails = locationDetails; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getResolutionPhotoUrl() { return resolutionPhotoUrl; }
    public void setResolutionPhotoUrl(String resolutionPhotoUrl) { this.resolutionPhotoUrl = resolutionPhotoUrl; }

    public Instant getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(Instant slaDeadline) { this.slaDeadline = slaDeadline; }

    public Boolean getSlaBreached() { return slaBreached; }
    public void setSlaBreached(Boolean slaBreached) { this.slaBreached = slaBreached; }

    public Boolean getEscalated() { return escalated; }
    public void setEscalated(Boolean escalated) { this.escalated = escalated; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    public FeedbackResponse getFeedback() { return feedback; }
    public void setFeedback(FeedbackResponse feedback) { this.feedback = feedback; }

    public List<AuditLogResponse> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<AuditLogResponse> auditLogs) { this.auditLogs = auditLogs; }
}
