package com.smartsociety.complaint.client;

public class StaffAvailabilityDto {
    private Long staffId;
    private String fullName;
    private String email;
    private String department;
    private Long societyId;
    private Boolean isAvailable;
    private Long activeTicketCount;

    public StaffAvailabilityDto() {
    }

    public StaffAvailabilityDto(Long staffId, String fullName, String email, String department,
                                Long societyId, Boolean isAvailable, Long activeTicketCount) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.societyId = societyId;
        this.isAvailable = isAvailable;
        this.activeTicketCount = activeTicketCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getSocietyId() {
        return societyId;
    }

    public void setSocietyId(Long societyId) {
        this.societyId = societyId;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Long getActiveTicketCount() {
        return activeTicketCount;
    }

    public void setActiveTicketCount(Long activeTicketCount) {
        this.activeTicketCount = activeTicketCount;
    }

    public static class Builder {
        private Long staffId;
        private String fullName;
        private String email;
        private String department;
        private Long societyId;
        private Boolean isAvailable;
        private Long activeTicketCount;

        public Builder staffId(Long staffId) {
            this.staffId = staffId;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder societyId(Long societyId) {
            this.societyId = societyId;
            return this;
        }

        public Builder isAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public Builder activeTicketCount(Long activeTicketCount) {
            this.activeTicketCount = activeTicketCount;
            return this;
        }

        public StaffAvailabilityDto build() {
            return new StaffAvailabilityDto(staffId, fullName, email, department, societyId, isAvailable, activeTicketCount);
        }
    }
}
