package com.smartsociety.complaint.dto;

import com.smartsociety.complaint.entity.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "New status is required")
    private ComplaintStatus status;

    private String notes;
    private String resolutionPhotoUrl;

    public UpdateStatusRequest() {}

    public UpdateStatusRequest(ComplaintStatus status, String notes, String resolutionPhotoUrl) {
        this.status = status;
        this.notes = notes;
        this.resolutionPhotoUrl = resolutionPhotoUrl;
    }

    public static UpdateStatusRequestBuilder builder() {
        return new UpdateStatusRequestBuilder();
    }

    public static class UpdateStatusRequestBuilder {
        private ComplaintStatus status;
        private String notes;
        private String resolutionPhotoUrl;

        public UpdateStatusRequestBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public UpdateStatusRequestBuilder notes(String notes) { this.notes = notes; return this; }
        public UpdateStatusRequestBuilder resolutionPhotoUrl(String resolutionPhotoUrl) { this.resolutionPhotoUrl = resolutionPhotoUrl; return this; }

        public UpdateStatusRequest build() {
            return new UpdateStatusRequest(status, notes, resolutionPhotoUrl);
        }
    }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getResolutionPhotoUrl() { return resolutionPhotoUrl; }
    public void setResolutionPhotoUrl(String resolutionPhotoUrl) { this.resolutionPhotoUrl = resolutionPhotoUrl; }
}
