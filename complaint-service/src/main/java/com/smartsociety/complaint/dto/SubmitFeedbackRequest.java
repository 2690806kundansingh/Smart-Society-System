package com.smartsociety.complaint.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SubmitFeedbackRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    private Integer rating;

    private String review;

    public SubmitFeedbackRequest() {}

    public SubmitFeedbackRequest(Integer rating, String review) {
        this.rating = rating;
        this.review = review;
    }

    public static SubmitFeedbackRequestBuilder builder() {
        return new SubmitFeedbackRequestBuilder();
    }

    public static class SubmitFeedbackRequestBuilder {
        private Integer rating;
        private String review;

        public SubmitFeedbackRequestBuilder rating(Integer rating) { this.rating = rating; return this; }
        public SubmitFeedbackRequestBuilder review(String review) { this.review = review; return this; }

        public SubmitFeedbackRequest build() {
            return new SubmitFeedbackRequest(rating, review);
        }
    }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
}
