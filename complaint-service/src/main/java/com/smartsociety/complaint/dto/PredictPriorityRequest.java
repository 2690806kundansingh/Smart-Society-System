package com.smartsociety.complaint.dto;

public class PredictPriorityRequest {
    private String title;
    private String description;
    private Long categoryId;

    public PredictPriorityRequest() {}

    public PredictPriorityRequest(String title, String description, Long categoryId) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
    }

    public static PredictPriorityRequestBuilder builder() {
        return new PredictPriorityRequestBuilder();
    }

    public static class PredictPriorityRequestBuilder {
        private String title;
        private String description;
        private Long categoryId;

        public PredictPriorityRequestBuilder title(String title) { this.title = title; return this; }
        public PredictPriorityRequestBuilder description(String description) { this.description = description; return this; }
        public PredictPriorityRequestBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }

        public PredictPriorityRequest build() {
            return new PredictPriorityRequest(title, description, categoryId);
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
