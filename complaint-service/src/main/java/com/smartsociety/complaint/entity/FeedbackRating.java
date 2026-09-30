package com.smartsociety.complaint.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "feedback_ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = "complaint_id")
})
public class FeedbackRating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_id", nullable = false, unique = true)
    private Long complaintId;

    @Column(name = "resident_id", nullable = false)
    private Long residentId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String review;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public FeedbackRating() {}

    public FeedbackRating(Long id, Long complaintId, Long residentId, Integer rating, String review, Instant createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.residentId = residentId;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
    }

    public static FeedbackRatingBuilder builder() {
        return new FeedbackRatingBuilder();
    }

    public static class FeedbackRatingBuilder {
        private Long id;
        private Long complaintId;
        private Long residentId;
        private Integer rating;
        private String review;
        private Instant createdAt;

        public FeedbackRatingBuilder id(Long id) { this.id = id; return this; }
        public FeedbackRatingBuilder complaintId(Long complaintId) { this.complaintId = complaintId; return this; }
        public FeedbackRatingBuilder residentId(Long residentId) { this.residentId = residentId; return this; }
        public FeedbackRatingBuilder rating(Integer rating) { this.rating = rating; return this; }
        public FeedbackRatingBuilder review(String review) { this.review = review; return this; }
        public FeedbackRatingBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public FeedbackRating build() {
            return new FeedbackRating(id, complaintId, residentId, rating, review, createdAt);
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
