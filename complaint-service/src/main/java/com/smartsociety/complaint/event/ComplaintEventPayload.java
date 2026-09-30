package com.smartsociety.complaint.event;

import java.io.Serializable;
import java.time.Instant;

public class ComplaintEventPayload implements Serializable {

    private String eventId;
    private ComplaintEventType eventType;
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

    public ComplaintEventPayload() {}

    public ComplaintEventPayload(String eventId, ComplaintEventType eventType, Long complaintId,
                                 String title, Long societyId, Long residentId, Long assignedStaffId,
                                 String status, String priority, Instant slaDeadline,
                                 Boolean slaBreached, Instant timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.complaintId = complaintId;
        this.title = title;
        this.societyId = societyId;
        this.residentId = residentId;
        this.assignedStaffId = assignedStaffId;
        this.status = status;
        this.priority = priority;
        this.slaDeadline = slaDeadline;
        this.slaBreached = slaBreached;
        this.timestamp = timestamp;
    }

    public static ComplaintEventPayloadBuilder builder() {
        return new ComplaintEventPayloadBuilder();
    }

    public static class ComplaintEventPayloadBuilder {
        private String eventId;
        private ComplaintEventType eventType;
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

        public ComplaintEventPayloadBuilder eventId(String eventId) { this.eventId = eventId; return this; }
        public ComplaintEventPayloadBuilder eventType(ComplaintEventType eventType) { this.eventType = eventType; return this; }
        public ComplaintEventPayloadBuilder complaintId(Long complaintId) { this.complaintId = complaintId; return this; }
        public ComplaintEventPayloadBuilder title(String title) { this.title = title; return this; }
        public ComplaintEventPayloadBuilder societyId(Long societyId) { this.societyId = societyId; return this; }
        public ComplaintEventPayloadBuilder residentId(Long residentId) { this.residentId = residentId; return this; }
        public ComplaintEventPayloadBuilder assignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public ComplaintEventPayloadBuilder status(String status) { this.status = status; return this; }
        public ComplaintEventPayloadBuilder priority(String priority) { this.priority = priority; return this; }
        public ComplaintEventPayloadBuilder slaDeadline(Instant slaDeadline) { this.slaDeadline = slaDeadline; return this; }
        public ComplaintEventPayloadBuilder slaBreached(Boolean slaBreached) { this.slaBreached = slaBreached; return this; }
        public ComplaintEventPayloadBuilder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public ComplaintEventPayload build() {
            return new ComplaintEventPayload(eventId, eventType, complaintId, title, societyId, residentId, assignedStaffId, status, priority, slaDeadline, slaBreached, timestamp);
        }
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public ComplaintEventType getEventType() { return eventType; }
    public void setEventType(ComplaintEventType eventType) { this.eventType = eventType; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getSocietyId() { return societyId; }
    public void setSocietyId(Long societyId) { this.societyId = societyId; }

    public Long getResidentId() { return residentId; }
    public void setResidentId(Long residentId) { this.residentId = residentId; }

    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Instant getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(Instant slaDeadline) { this.slaDeadline = slaDeadline; }

    public Boolean getSlaBreached() { return slaBreached; }
    public void setSlaBreached(Boolean slaBreached) { this.slaBreached = slaBreached; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
