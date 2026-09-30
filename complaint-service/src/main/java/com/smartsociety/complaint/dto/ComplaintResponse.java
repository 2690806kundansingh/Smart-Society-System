package com.smartsociety.complaint.dto;

import com.smartsociety.complaint.entity.Category;
import com.smartsociety.complaint.entity.ComplaintStatus;
import com.smartsociety.complaint.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
