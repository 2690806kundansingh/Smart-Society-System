package com.smartsociety.notification.dto;

import java.io.Serializable;
import java.time.Instant;

public class NotificationDto implements Serializable {

    private String id;
    private String title;
    private String message;
    private String type; // INFO, WARNING, SUCCESS, URGENT
    private Long complaintId;
    private String recipientRole;
    private Long targetUserId;
    private Long societyId;
    private String status;
    private Instant timestamp;

    public NotificationDto() {
    }

    public NotificationDto(String id, String title, String message, String type, Long complaintId,
                           String recipientRole, Long targetUserId, Long societyId, String status, Instant timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.complaintId = complaintId;
        this.recipientRole = recipientRole;
        this.targetUserId = targetUserId;
        this.societyId = societyId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Long getSocietyId() {
        return societyId;
    }

    public void setSocietyId(Long societyId) {
        this.societyId = societyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static class Builder {
        private String id;
        private String title;
        private String message;
        private String type;
        private Long complaintId;
        private String recipientRole;
        private Long targetUserId;
        private Long societyId;
        private String status;
        private Instant timestamp;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder complaintId(Long complaintId) {
            this.complaintId = complaintId;
            return this;
        }

        public Builder recipientRole(String recipientRole) {
            this.recipientRole = recipientRole;
            return this;
        }

        public Builder targetUserId(Long targetUserId) {
            this.targetUserId = targetUserId;
            return this;
        }

        public Builder societyId(Long societyId) {
            this.societyId = societyId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public NotificationDto build() {
            return new NotificationDto(id, title, message, type, complaintId, recipientRole, targetUserId, societyId, status, timestamp);
        }
    }
}
