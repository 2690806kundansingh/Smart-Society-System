package com.smartsociety.complaint.controller;

import com.smartsociety.complaint.dto.*;
import com.smartsociety.complaint.service.ComplaintService;
import com.smartsociety.complaint.service.DashboardStatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final DashboardStatsService dashboardStatsService;

    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(
            @Valid @RequestBody CreateComplaintRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId,
            @RequestHeader(value = "X-User-Society", required = false, defaultValue = "1") Long societyId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(complaintService.createComplaint(request, userId, societyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> listComplaints(
            @RequestParam(required = false) Long societyId,
            @RequestParam(required = false) Long residentId,
            @RequestParam(required = false) Long staffId,
            @RequestHeader(value = "X-User-Society", required = false) Long headerSocietyId) {

        if (residentId != null) {
            return ResponseEntity.ok(complaintService.getComplaintsByResident(residentId));
        }
        if (staffId != null) {
            return ResponseEntity.ok(complaintService.getComplaintsByStaff(staffId));
        }

        Long targetSocietyId = societyId != null ? societyId : (headerSocietyId != null ? headerSocietyId : 1L);
        return ResponseEntity.ok(complaintService.getComplaintsBySociety(targetSocietyId));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ComplaintResponse> assignStaff(
            @PathVariable Long id,
            @Valid @RequestBody AssignStaffRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long performedBy,
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "ADMIN") String role) {
        return ResponseEntity.ok(complaintService.assignStaff(id, request, performedBy, role));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long performedBy,
            @RequestHeader(value = "X-User-Role", required = false, defaultValue = "STAFF") String role) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request, performedBy, role));
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<FeedbackResponse> submitFeedback(
            @PathVariable Long id,
            @Valid @RequestBody SubmitFeedbackRequest request,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long residentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(complaintService.submitFeedback(id, request, residentId));
    }

    @PostMapping("/predict-priority")
    public ResponseEntity<PredictPriorityResponse> predictPriority(
            @RequestBody PredictPriorityRequest request) {
        return ResponseEntity.ok(complaintService.predictPriority(request));
    }

    @GetMapping("/society/{societyId}/escalated")
    public ResponseEntity<List<ComplaintResponse>> getEscalatedComplaints(@PathVariable Long societyId) {
        return ResponseEntity.ok(complaintService.getEscalatedComplaints(societyId));
    }

    @GetMapping("/society/{societyId}/stats")
    public ResponseEntity<DashboardStatsResponse> getSocietyStats(@PathVariable Long societyId) {
        return ResponseEntity.ok(dashboardStatsService.getSocietyStats(societyId));
    }
}
