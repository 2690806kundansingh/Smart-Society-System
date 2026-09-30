package com.smartsociety.complaint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictPriorityRequest {
    private String title;
    private String description;
    private Long categoryId;
}
