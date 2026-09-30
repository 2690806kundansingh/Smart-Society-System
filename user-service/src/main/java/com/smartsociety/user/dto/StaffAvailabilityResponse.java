package com.smartsociety.user.dto;

import com.smartsociety.user.entity.Department;

public class StaffAvailabilityResponse {

    private Long staffId;
    private String fullName;
    private String email;
    private Department department;
    private Long societyId;
    private Boolean isAvailable;
    private Long activeTicketCount;

    public StaffAvailabilityResponse() {}

    public StaffAvailabilityResponse(Long staffId, String fullName, String email, Department department,
                                     Long societyId, Boolean isAvailable, Long activeTicketCount) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.societyId = societyId;
        this.isAvailable = isAvailable;
        this.activeTicketCount = activeTicketCount;
    }

    public static StaffAvailabilityResponseBuilder builder() {
        return new StaffAvailabilityResponseBuilder();
    }

    public static class StaffAvailabilityResponseBuilder {
        private Long staffId;
        private String fullName;
        private String email;
        private Department department;
        private Long societyId;
        private Boolean isAvailable;
        private Long activeTicketCount;

        public StaffAvailabilityResponseBuilder staffId(Long staffId) { this.staffId = staffId; return this; }
        public StaffAvailabilityResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public StaffAvailabilityResponseBuilder email(String email) { this.email = email; return this; }
        public StaffAvailabilityResponseBuilder department(Department department) { this.department = department; return this; }
        public StaffAvailabilityResponseBuilder societyId(Long societyId) { this.societyId = societyId; return this; }
        public StaffAvailabilityResponseBuilder isAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; return this; }
        public StaffAvailabilityResponseBuilder activeTicketCount(Long activeTicketCount) { this.activeTicketCount = activeTicketCount; return this; }

        public StaffAvailabilityResponse build() {
            return new StaffAvailabilityResponse(staffId, fullName, email, department, societyId, isAvailable, activeTicketCount);
        }
    }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Long getSocietyId() { return societyId; }
    public void setSocietyId(Long societyId) { this.societyId = societyId; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Long getActiveTicketCount() { return activeTicketCount; }
    public void setActiveTicketCount(Long activeTicketCount) { this.activeTicketCount = activeTicketCount; }
}
