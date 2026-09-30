package com.smartsociety.complaint.dto;

import jakarta.validation.constraints.NotNull;

public class AssignStaffRequest {

    @NotNull(message = "Staff ID is required")
    private Long staffId;

    private String notes;

    public AssignStaffRequest() {}

    public AssignStaffRequest(Long staffId, String notes) {
        this.staffId = staffId;
        this.notes = notes;
    }

    public static AssignStaffRequestBuilder builder() {
        return new AssignStaffRequestBuilder();
    }

    public static class AssignStaffRequestBuilder {
        private Long staffId;
        private String notes;

        public AssignStaffRequestBuilder staffId(Long staffId) { this.staffId = staffId; return this; }
        public AssignStaffRequestBuilder notes(String notes) { this.notes = notes; return this; }

        public AssignStaffRequest build() {
            return new AssignStaffRequest(staffId, notes);
        }
    }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
