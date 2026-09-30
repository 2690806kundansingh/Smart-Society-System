package com.smartsociety.user.dto;

import com.smartsociety.user.entity.Department;
import java.util.List;

public class UserProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Long societyId;
    private List<String> roles;
    private Department department;
    private String apartment;
    private Boolean isAvailable;
    private Boolean isActive;

    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String email, String fullName, String phoneNumber,
                               Long societyId, List<String> roles, Department department,
                               String apartment, Boolean isAvailable, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.societyId = societyId;
        this.roles = roles;
        this.department = department;
        this.apartment = apartment;
        this.isAvailable = isAvailable;
        this.isActive = isActive;
    }

    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
    }

    public static class UserProfileResponseBuilder {
        private Long id;
        private String email;
        private String fullName;
        private String phoneNumber;
        private Long societyId;
        private List<String> roles;
        private Department department;
        private String apartment;
        private Boolean isAvailable;
        private Boolean isActive;

        public UserProfileResponseBuilder id(Long id) { this.id = id; return this; }
        public UserProfileResponseBuilder email(String email) { this.email = email; return this; }
        public UserProfileResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserProfileResponseBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public UserProfileResponseBuilder societyId(Long societyId) { this.societyId = societyId; return this; }
        public UserProfileResponseBuilder roles(List<String> roles) { this.roles = roles; return this; }
        public UserProfileResponseBuilder department(Department department) { this.department = department; return this; }
        public UserProfileResponseBuilder apartment(String apartment) { this.apartment = apartment; return this; }
        public UserProfileResponseBuilder isAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; return this; }
        public UserProfileResponseBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public UserProfileResponse build() {
            return new UserProfileResponse(id, email, fullName, phoneNumber, societyId, roles, department, apartment, isAvailable, isActive);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Long getSocietyId() { return societyId; }
    public void setSocietyId(Long societyId) { this.societyId = societyId; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public String getApartment() { return apartment; }
    public void setApartment(String apartment) { this.apartment = apartment; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
