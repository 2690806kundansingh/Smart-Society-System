package com.smartsociety.complaint.dto;

import java.io.Serializable;
import java.util.Map;

public class DashboardStatsResponse implements Serializable {

    private Long totalComplaints;
    private Long resolvedCount;
    private Long pendingCount;
    private Long slaBreachedCount;
    private Double slaBreachedPercentage;
    private Double averageResolutionHours;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> statusDistribution;

    public DashboardStatsResponse() {}

    public DashboardStatsResponse(Long totalComplaints, Long resolvedCount, Long pendingCount,
                                  Long slaBreachedCount, Double slaBreachedPercentage,
                                  Double averageResolutionHours, Map<String, Long> categoryDistribution,
                                  Map<String, Long> statusDistribution) {
        this.totalComplaints = totalComplaints;
        this.resolvedCount = resolvedCount;
        this.pendingCount = pendingCount;
        this.slaBreachedCount = slaBreachedCount;
        this.slaBreachedPercentage = slaBreachedPercentage;
        this.averageResolutionHours = averageResolutionHours;
        this.categoryDistribution = categoryDistribution;
        this.statusDistribution = statusDistribution;
    }

    public static DashboardStatsResponseBuilder builder() {
        return new DashboardStatsResponseBuilder();
    }

    public static class DashboardStatsResponseBuilder {
        private Long totalComplaints;
        private Long resolvedCount;
        private Long pendingCount;
        private Long slaBreachedCount;
        private Double slaBreachedPercentage;
        private Double averageResolutionHours;
        private Map<String, Long> categoryDistribution;
        private Map<String, Long> statusDistribution;

        public DashboardStatsResponseBuilder totalComplaints(Long totalComplaints) { this.totalComplaints = totalComplaints; return this; }
        public DashboardStatsResponseBuilder resolvedCount(Long resolvedCount) { this.resolvedCount = resolvedCount; return this; }
        public DashboardStatsResponseBuilder pendingCount(Long pendingCount) { this.pendingCount = pendingCount; return this; }
        public DashboardStatsResponseBuilder slaBreachedCount(Long slaBreachedCount) { this.slaBreachedCount = slaBreachedCount; return this; }
        public DashboardStatsResponseBuilder slaBreachedPercentage(Double slaBreachedPercentage) { this.slaBreachedPercentage = slaBreachedPercentage; return this; }
        public DashboardStatsResponseBuilder averageResolutionHours(Double averageResolutionHours) { this.averageResolutionHours = averageResolutionHours; return this; }
        public DashboardStatsResponseBuilder categoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; return this; }
        public DashboardStatsResponseBuilder statusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; return this; }

        public DashboardStatsResponse build() {
            return new DashboardStatsResponse(totalComplaints, resolvedCount, pendingCount, slaBreachedCount, slaBreachedPercentage, averageResolutionHours, categoryDistribution, statusDistribution);
        }
    }

    public Long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(Long totalComplaints) { this.totalComplaints = totalComplaints; }

    public Long getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(Long resolvedCount) { this.resolvedCount = resolvedCount; }

    public Long getPendingCount() { return pendingCount; }
    public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }

    public Long getSlaBreachedCount() { return slaBreachedCount; }
    public void setSlaBreachedCount(Long slaBreachedCount) { this.slaBreachedCount = slaBreachedCount; }

    public Double getSlaBreachedPercentage() { return slaBreachedPercentage; }
    public void setSlaBreachedPercentage(Double slaBreachedPercentage) { this.slaBreachedPercentage = slaBreachedPercentage; }

    public Double getAverageResolutionHours() { return averageResolutionHours; }
    public void setAverageResolutionHours(Double averageResolutionHours) { this.averageResolutionHours = averageResolutionHours; }

    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }
}
