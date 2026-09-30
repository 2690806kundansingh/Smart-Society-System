package com.smartsociety.complaint.dto;

import com.smartsociety.complaint.entity.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
