package com.smartsociety.complaint.service;

import com.smartsociety.complaint.client.StaffAvailabilityDto;
import com.smartsociety.complaint.client.UserClient;
import com.smartsociety.complaint.dto.*;
import com.smartsociety.complaint.engine.PriorityPredictionEngine;
import com.smartsociety.complaint.entity.*;
import com.smartsociety.complaint.event.ComplaintEventPayload;
import com.smartsociety.complaint.event.ComplaintEventProducer;
import com.smartsociety.complaint.event.ComplaintEventType;
import com.smartsociety.complaint.exception.InvalidStateTransitionException;
import com.smartsociety.complaint.exception.ResourceNotFoundException;
import com.smartsociety.complaint.exception.StaffNotAvailableException;
import com.smartsociety.complaint.repository.CategoryRepository;
import com.smartsociety.complaint.repository.ComplaintAuditLogRepository;
import com.smartsociety.complaint.repository.ComplaintRepository;
import com.smartsociety.complaint.repository.FeedbackRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintAuditLogRepository auditLogRepository;
    private final FeedbackRatingRepository feedbackRatingRepository;
    private final PriorityPredictionEngine priorityPredictionEngine;
    private final ComplaintEventProducer eventProducer;
    private final DashboardStatsService dashboardStatsService;
    private final UserClient userClient;

    @Transactional
    public ComplaintResponse createComplaint(CreateComplaintRequest request, Long residentId, Long societyId) {
        Long targetResidentId = (request.getResidentId() != null) ? request.getResidentId() : residentId;
        Long targetSocietyId = (request.getSocietyId() != null) ? request.getSocietyId() : societyId;

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        // Run PriorityPredictionEngine to determine priority and SLA window
        Priority predictedPriority = priorityPredictionEngine.predictPriority(
                request.getTitle(), request.getDescription(), category
        );
        Instant slaDeadline = priorityPredictionEngine.calculateSlaDeadline(predictedPriority);

        Complaint complaint = Complaint.builder()
                .societyId(targetSocietyId)
                .residentId(targetResidentId)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .locationDetails(request.getLocationDetails())
                .photoUrl(request.getPhotoUrl())
                .priority(predictedPriority)
                .status(ComplaintStatus.OPEN)
                .slaDeadline(slaDeadline)
                .slaBreached(false)
                .escalated(false)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        // Record Audit Log
        recordAuditLog(saved.getId(), "CREATED", targetResidentId, "RESIDENT", null, ComplaintStatus.OPEN, "Complaint logged");

        // Emit Kafka Event
        emitEvent(saved, ComplaintEventType.CREATED);

        // Evict cached stats
        dashboardStatsService.evictStatsCache(targetSocietyId);

        return mapToResponse(saved);
    }

    @Transactional
    public ComplaintResponse assignStaff(Long complaintId, AssignStaffRequest request, Long performedBy, String performedByRole) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));

        // Inter-service check via OpenFeign with Resilience4j
        StaffAvailabilityDto staffAvailability = userClient.getStaffAvailability(request.getStaffId());
        if (staffAvailability == null || Boolean.FALSE.equals(staffAvailability.getIsAvailable())) {
            throw new StaffNotAvailableException("Staff member #" + request.getStaffId() + " is currently unavailable for assignment.");
        }

        ComplaintStatus previousStatus = complaint.getStatus();
        complaint.setAssignedStaffId(request.getStaffId());
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        // Optimistic locking (@Version) guarantees atomic state check
        Complaint updated = complaintRepository.save(complaint);

        String note = (request.getNotes() != null) ? request.getNotes() : "Staff assigned: " + staffAvailability.getFullName();
        recordAuditLog(updated.getId(), "ASSIGNED", performedBy, performedByRole, previousStatus, ComplaintStatus.ASSIGNED, note);

        emitEvent(updated, ComplaintEventType.ASSIGNED);
        dashboardStatsService.evictStatsCache(updated.getSocietyId());

        return mapToResponse(updated);
    }

    @Transactional
    public ComplaintResponse updateStatus(Long complaintId, UpdateStatusRequest request, Long performedBy, String performedByRole) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));

        ComplaintStatus currentStatus = complaint.getStatus();
        ComplaintStatus newStatus = request.getStatus();

        validateStateTransition(currentStatus, newStatus);

        complaint.setStatus(newStatus);
        if (request.getNotes() != null) {
            complaint.setResolutionNotes(request.getNotes());
        }
        if (request.getResolutionPhotoUrl() != null) {
            complaint.setResolutionPhotoUrl(request.getResolutionPhotoUrl());
        }

        if (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.CLOSED) {
            complaint.setResolvedAt(Instant.now());
        }

        Complaint updated = complaintRepository.save(complaint);

        recordAuditLog(updated.getId(), "STATUS_CHANGE", performedBy, performedByRole, currentStatus, newStatus, request.getNotes());

        emitEvent(updated, ComplaintEventType.STATUS_UPDATED);
        dashboardStatsService.evictStatsCache(updated.getSocietyId());

        return mapToResponse(updated);
    }

    @Transactional
    public FeedbackResponse submitFeedback(Long complaintId, SubmitFeedbackRequest request, Long residentId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));

        if (complaint.getStatus() != ComplaintStatus.RESOLVED && complaint.getStatus() != ComplaintStatus.CLOSED) {
            throw new InvalidStateTransitionException("Feedback can only be provided for RESOLVED or CLOSED tickets.");
        }

        Optional<FeedbackRating> existing = feedbackRatingRepository.findByComplaintId(complaintId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Feedback has already been submitted for this complaint.");
        }

        FeedbackRating feedback = FeedbackRating.builder()
                .complaintId(complaintId)
                .residentId(residentId)
                .rating(request.getRating())
                .review(request.getReview())
                .build();

        FeedbackRating savedFeedback = feedbackRatingRepository.save(feedback);

        recordAuditLog(complaintId, "FEEDBACK_SUBMITTED", residentId, "RESIDENT", complaint.getStatus(), complaint.getStatus(),
                "Rated " + request.getRating() + " stars");

        dashboardStatsService.evictStatsCache(complaint.getSocietyId());

        return FeedbackResponse.builder()
                .id(savedFeedback.getId())
                .complaintId(savedFeedback.getComplaintId())
                .residentId(savedFeedback.getResidentId())
                .rating(savedFeedback.getRating())
                .review(savedFeedback.getReview())
                .createdAt(savedFeedback.getCreatedAt())
                .build();
    }

    public PredictPriorityResponse predictPriority(PredictPriorityRequest request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        Priority predicted = priorityPredictionEngine.predictPriority(
                request.getTitle(), request.getDescription(), category
        );

        String reason = switch (predicted) {
            case HIGH -> "Critical issue detected (Emergency / safety / core utility failure). Target SLA: 2 Hours.";
            case MEDIUM -> "Standard operational issue. Target SLA: 12 Hours.";
            case LOW -> "Routine maintenance or cosmetic request. Target SLA: 48 Hours.";
        };

        return PredictPriorityResponse.builder()
                .priority(predicted)
                .slaHours(predicted.getSlaHours())
                .reason(reason)
                .build();
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + id));
        return mapToResponse(complaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsBySociety(Long societyId) {
        return complaintRepository.findBySocietyIdOrderByCreatedAtDesc(societyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByResident(Long residentId) {
        return complaintRepository.findByResidentIdOrderByCreatedAtDesc(residentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByStaff(Long staffId) {
        return complaintRepository.findByAssignedStaffIdOrderByCreatedAtDesc(staffId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getEscalatedComplaints(Long societyId) {
        return complaintRepository.findBySocietyIdAndEscalatedTrueOrderByCreatedAtDesc(societyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateStateTransition(ComplaintStatus from, ComplaintStatus to) {
        if (from == to) {
            return;
        }

        boolean valid = switch (from) {
            case OPEN -> (to == ComplaintStatus.ASSIGNED || to == ComplaintStatus.IN_PROGRESS);
            case ASSIGNED -> (to == ComplaintStatus.IN_PROGRESS || to == ComplaintStatus.ASSIGNED);
            case IN_PROGRESS -> (to == ComplaintStatus.RESOLVED);
            case RESOLVED -> (to == ComplaintStatus.CLOSED || to == ComplaintStatus.IN_PROGRESS);
            case CLOSED -> false;
        };

        if (!valid) {
            throw new InvalidStateTransitionException(
                    "Cannot transition complaint state from " + from + " to " + to
            );
        }
    }

    private void recordAuditLog(Long complaintId, String action, Long performedBy, String role,
                                ComplaintStatus prev, ComplaintStatus next, String notes) {
        ComplaintAuditLog logEntry = ComplaintAuditLog.builder()
                .complaintId(complaintId)
                .action(action)
                .performedBy(performedBy)
                .performedByRole(role)
                .previousStatus(prev)
                .newStatus(next)
                .notes(notes)
                .build();
        auditLogRepository.save(logEntry);
    }

    private void emitEvent(Complaint complaint, ComplaintEventType eventType) {
        ComplaintEventPayload payload = ComplaintEventPayload.builder()
                .eventType(eventType)
                .complaintId(complaint.getId())
                .title(complaint.getTitle())
                .societyId(complaint.getSocietyId())
                .residentId(complaint.getResidentId())
                .assignedStaffId(complaint.getAssignedStaffId())
                .status(complaint.getStatus().name())
                .priority(complaint.getPriority().name())
                .slaDeadline(complaint.getSlaDeadline())
                .slaBreached(complaint.getSlaBreached())
                .timestamp(Instant.now())
                .build();
        eventProducer.publishEvent(payload);
    }

    private ComplaintResponse mapToResponse(Complaint c) {
        FeedbackResponse feedbackResponse = feedbackRatingRepository.findByComplaintId(c.getId())
                .map(f -> FeedbackResponse.builder()
                        .id(f.getId())
                        .complaintId(f.getComplaintId())
                        .residentId(f.getResidentId())
                        .rating(f.getRating())
                        .review(f.getReview())
                        .createdAt(f.getCreatedAt())
                        .build())
                .orElse(null);

        List<AuditLogResponse> logs = auditLogRepository.findByComplaintIdOrderByCreatedAtDesc(c.getId()).stream()
                .map(l -> AuditLogResponse.builder()
                        .id(l.getId())
                        .complaintId(l.getComplaintId())
                        .action(l.getAction())
                        .performedBy(l.getPerformedBy())
                        .performedByRole(l.getPerformedByRole())
                        .previousStatus(l.getPreviousStatus())
                        .newStatus(l.getNewStatus())
                        .notes(l.getNotes())
                        .createdAt(l.getCreatedAt())
                        .build())
                .toList();

        return ComplaintResponse.builder()
                .id(c.getId())
                .societyId(c.getSocietyId())
                .residentId(c.getResidentId())
                .category(c.getCategory())
                .title(c.getTitle())
                .description(c.getDescription())
                .locationDetails(c.getLocationDetails())
                .photoUrl(c.getPhotoUrl())
                .priority(c.getPriority())
                .status(c.getStatus())
                .assignedStaffId(c.getAssignedStaffId())
                .resolutionNotes(c.getResolutionNotes())
                .resolutionPhotoUrl(c.getResolutionPhotoUrl())
                .slaDeadline(c.getSlaDeadline())
                .slaBreached(c.getSlaBreached())
                .escalated(c.getEscalated())
                .version(c.getVersion())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .resolvedAt(c.getResolvedAt())
                .feedback(feedbackResponse)
                .auditLogs(logs)
                .build();
    }
}
