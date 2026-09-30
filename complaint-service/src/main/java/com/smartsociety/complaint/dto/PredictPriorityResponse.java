package com.smartsociety.complaint.dto;

import com.smartsociety.complaint.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictPriorityResponse {
    private Priority priority;
    private Integer slaHours;
    private String reason;
}
