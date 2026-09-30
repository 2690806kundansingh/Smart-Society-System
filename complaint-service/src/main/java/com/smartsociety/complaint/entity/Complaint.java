package com.smartsociety.complaint.entity;

import jakarta.persistence.*;
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
    private Boolean slaBreached = false;

    @Column(nullable = false)
    private Boolean escalated = false;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    public Complaint() {}

    public Complaint(Long id, Long societyId, Long residentId, Category category, String title,
                     String description, String locationDetails, String photoUrl, Priority priority,
                     ComplaintStatus status, Long assignedStaffId, String resolutionNotes,
                     String resolutionPhotoUrl, Instant slaDeadline, Boolean slaBreached,
                     Boolean escalated, Long version, Instant createdAt, Instant updatedAt, Instant resolvedAt) {
        this.id = id;
        this.societyId = societyId;
        this.residentId = residentId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.locationDetails = locationDetails;
        this.photoUrl = photoUrl;
        this.priority = priority;
        this.status = status != null ? status : ComplaintStatus.OPEN;
        this.assignedStaffId = assignedStaffId;
        this.resolutionNotes = resolutionNotes;
        this.resolutionPhotoUrl = resolutionPhotoUrl;
        this.slaDeadline = slaDeadline;
        this.slaBreached = slaBreached != null ? slaBreached : false;
        this.escalated = escalated != null ? escalated : false;
        this.version = version != null ? version : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
    }

    public static ComplaintBuilder builder() {
        return new ComplaintBuilder();
    }

    public static class ComplaintBuilder {
        private Long id;
        private Long societyId;
        private Long residentId;
        private Category category;
        private String title;
        private String description;
        private String locationDetails;
        private String photoUrl;
        private Priority priority;
        private ComplaintStatus status = ComplaintStatus.OPEN;
        private Long assignedStaffId;
        private String resolutionNotes;
        private String resolutionPhotoUrl;
        private Instant slaDeadline;
        private Boolean slaBreached = false;
        private Boolean escalated = false;
        private Long version = 0L;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant resolvedAt;

        public ComplaintBuilder id(Long id) { this.id = id; return this; }
        public ComplaintBuilder societyId(Long societyId) { this.societyId = societyId; return this; }
        public ComplaintBuilder residentId(Long residentId) { this.residentId = residentId; return this; }
        public ComplaintBuilder category(Category category) { this.category = category; return this; }
        public ComplaintBuilder title(String title) { this.title = title; return this; }
        public ComplaintBuilder description(String description) { this.description = description; return this; }
        public ComplaintBuilder locationDetails(String locationDetails) { this.locationDetails = locationDetails; return this; }
        public ComplaintBuilder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }
        public ComplaintBuilder priority(Priority priority) { this.priority = priority; return this; }
        public ComplaintBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintBuilder assignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public ComplaintBuilder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public ComplaintBuilder resolutionPhotoUrl(String resolutionPhotoUrl) { this.resolutionPhotoUrl = resolutionPhotoUrl; return this; }
        public ComplaintBuilder slaDeadline(Instant slaDeadline) { this.slaDeadline = slaDeadline; return this; }
        public ComplaintBuilder slaBreached(Boolean slaBreached) { this.slaBreached = slaBreached; return this; }
        public ComplaintBuilder escalated(Boolean escalated) { this.escalated = escalated; return this; }
        public ComplaintBuilder version(Long version) { this.version = version; return this; }
        public ComplaintBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public ComplaintBuilder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public Complaint build() {
            return new Complaint(id, societyId, residentId, category, title, description, locationDetails, photoUrl,
                    priority, status, assignedStaffId, resolutionNotes, resolutionPhotoUrl, slaDeadline, slaBreached,
                    escalated, version, createdAt, updatedAt, resolvedAt);
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
}
