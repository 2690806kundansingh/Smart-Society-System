package com.smartsociety.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateComplaintRequest {

    private Long societyId;
    private Long residentId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must provide at least 10 characters")
    private String description;

    private String locationDetails;
    private String photoUrl;

    public CreateComplaintRequest() {}

    public CreateComplaintRequest(Long societyId, Long residentId, Long categoryId, String title,
                                  String description, String locationDetails, String photoUrl) {
        this.societyId = societyId;
        this.residentId = residentId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.locationDetails = locationDetails;
        this.photoUrl = photoUrl;
    }

    public static CreateComplaintRequestBuilder builder() {
        return new CreateComplaintRequestBuilder();
    }

    public static class CreateComplaintRequestBuilder {
        private Long societyId;
        private Long residentId;
        private Long categoryId;
        private String title;
        private String description;
        private String locationDetails;
        private String photoUrl;

        public CreateComplaintRequestBuilder societyId(Long societyId) { this.societyId = societyId; return this; }
        public CreateComplaintRequestBuilder residentId(Long residentId) { this.residentId = residentId; return this; }
        public CreateComplaintRequestBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public CreateComplaintRequestBuilder title(String title) { this.title = title; return this; }
        public CreateComplaintRequestBuilder description(String description) { this.description = description; return this; }
        public CreateComplaintRequestBuilder locationDetails(String locationDetails) { this.locationDetails = locationDetails; return this; }
        public CreateComplaintRequestBuilder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }

        public CreateComplaintRequest build() {
            return new CreateComplaintRequest(societyId, residentId, categoryId, title, description, locationDetails, photoUrl);
        }
    }

    public Long getSocietyId() { return societyId; }
    public void setSocietyId(Long societyId) { this.societyId = societyId; }

    public Long getResidentId() { return residentId; }
    public void setResidentId(Long residentId) { this.residentId = residentId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationDetails() { return locationDetails; }
    public void setLocationDetails(String locationDetails) { this.locationDetails = locationDetails; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
