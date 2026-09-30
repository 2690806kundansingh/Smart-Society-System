package com.smartsociety.complaint.dto;

import java.io.Serializable;
import java.time.Instant;

public class FeedbackResponse implements Serializable {
    private Long id;
    private Long complaintId;
    private Long residentId;
    private Integer rating;
    private String review;
    private Instant createdAt;

    public FeedbackResponse() {}

    public FeedbackResponse(Long id, Long complaintId, Long residentId, Integer rating, String review, Instant createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.residentId = residentId;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
    }

    public static FeedbackResponseBuilder builder() {
        return new FeedbackResponseBuilder();
    }

    public static class FeedbackResponseBuilder {
        private Long id;
        private Long complaintId;
        private Long residentId;
        private Integer rating;
        private String review;
        private Instant createdAt;

        public FeedbackResponseBuilder id(Long id) { this.id = id; return this; }
        public FeedbackResponseBuilder complaintId(Long complaintId) { this.complaintId = complaintId; return this; }
        public FeedbackResponseBuilder residentId(Long residentId) { this.residentId = residentId; return this; }
        public FeedbackResponseBuilder rating(Integer rating) { this.rating = rating; return this; }
        public FeedbackResponseBuilder review(String review) { this.review = review; return this; }
        public FeedbackResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public FeedbackResponse build() {
            return new FeedbackResponse(id, complaintId, residentId, rating, review, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public Long getResidentId() { return residentId; }
    public void setResidentId(Long residentId) { this.residentId = residentId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
