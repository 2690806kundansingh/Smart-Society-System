package com.smartsociety.complaint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse implements Serializable {

    private Long totalComplaints;
    private Long resolvedCount;
    private Long pendingCount;
    private Long slaBreachedCount;
    private Double slaBreachedPercentage;
    private Double averageResolutionHours;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> statusDistribution;
}
