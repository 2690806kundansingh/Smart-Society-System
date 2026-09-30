package com.smartsociety.complaint.service;

import com.smartsociety.complaint.dto.DashboardStatsResponse;
import com.smartsociety.complaint.entity.Complaint;
import com.smartsociety.complaint.entity.ComplaintStatus;
import com.smartsociety.complaint.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardStatsService {

    private static final Logger log = LoggerFactory.getLogger(DashboardStatsService.class);
    private final ComplaintRepository complaintRepository;

    public DashboardStatsService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Cacheable(value = "society-stats", key = "#societyId")
    @Transactional(readOnly = true)
    public DashboardStatsResponse getSocietyStats(Long societyId) {
        log.info("Calculating society statistics from database (cache miss) for societyId: {}", societyId);

        List<Complaint> allComplaints = complaintRepository.findBySocietyIdOrderByCreatedAtDesc(societyId);
        long totalComplaints = allComplaints.size();

        long resolvedCount = allComplaints.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED)
                .count();

        long pendingCount = totalComplaints - resolvedCount;

        long slaBreachedCount = allComplaints.stream()
                .filter(c -> Boolean.TRUE.equals(c.getSlaBreached()))
                .count();

        double slaBreachedPercentage = totalComplaints > 0
                ? Math.round(((double) slaBreachedCount / totalComplaints * 100.0) * 10.0) / 10.0
                : 0.0;

        // Calculate average resolution time for resolved complaints
        double totalHours = 0.0;
        int resolvedWithDuration = 0;
        for (Complaint c : allComplaints) {
            if (c.getResolvedAt() != null && c.getCreatedAt() != null) {
                long durationSeconds = Duration.between(c.getCreatedAt(), c.getResolvedAt()).getSeconds();
                totalHours += (durationSeconds / 3600.0);
                resolvedWithDuration++;
            }
        }
        double avgResolutionHours = resolvedWithDuration > 0
                ? Math.round((totalHours / resolvedWithDuration) * 10.0) / 10.0
                : 0.0;

        // Category distribution
        Map<String, Long> categoryDistribution = new HashMap<>();
        for (Complaint c : allComplaints) {
            String catName = c.getCategory() != null ? c.getCategory().getName() : "General";
            categoryDistribution.put(catName, categoryDistribution.getOrDefault(catName, 0L) + 1);
        }

        // Status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        for (Complaint c : allComplaints) {
            String statusName = c.getStatus().name();
            statusDistribution.put(statusName, statusDistribution.getOrDefault(statusName, 0L) + 1);
        }

        return DashboardStatsResponse.builder()
                .totalComplaints(totalComplaints)
                .resolvedCount(resolvedCount)
                .pendingCount(pendingCount)
                .slaBreachedCount(slaBreachedCount)
                .slaBreachedPercentage(slaBreachedPercentage)
                .averageResolutionHours(avgResolutionHours)
                .categoryDistribution(categoryDistribution)
                .statusDistribution(statusDistribution)
                .build();
    }

    @CacheEvict(value = "society-stats", key = "#societyId")
    public void evictStatsCache(Long societyId) {
        log.info("Evicting society statistics cache for societyId: {}", societyId);
    }
}
