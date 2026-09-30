package com.smartsociety.notification.event;

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
public class ComplaintEventPayload implements Serializable {

    private String eventId;
    private String eventType;
    private Long complaintId;
    private String title;
    private Long societyId;
    private Long residentId;
    private Long assignedStaffId;
    private String status;
    private String priority;
    private Instant slaDeadline;
    private Boolean slaBreached;
    private Instant timestamp;
}
