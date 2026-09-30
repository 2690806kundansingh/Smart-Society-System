package com.smartsociety.complaint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse implements Serializable {
    private Long id;
    private Long complaintId;
    private Long residentId;
    private Integer rating;
    private String review;
    private Instant createdAt;
}
