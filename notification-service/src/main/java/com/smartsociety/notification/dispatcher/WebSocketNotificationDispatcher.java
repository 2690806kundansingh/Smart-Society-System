package com.smartsociety.notification.dispatcher;

import com.smartsociety.notification.dto.NotificationDto;
import com.smartsociety.notification.event.ComplaintEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationDispatcher {

    private final SimpMessagingTemplate messagingTemplate;
    private final List<NotificationDto> recentNotifications = new CopyOnWriteArrayList<>();

    public void dispatch(ComplaintEventPayload payload) {
        String eventType = payload.getEventType();
        String type = "INFO";
        String title;
        String message;

        switch (eventType) {
            case "CREATED" -> {
                title = "New Complaint Logged";
                message = "Complaint #" + payload.getComplaintId() + " (" + payload.getTitle() + ") has been submitted.";
                type = "INFO";
            }
            case "ASSIGNED" -> {
                title = "Complaint Assigned";
                message = "Complaint #" + payload.getComplaintId() + " has been assigned to staff member #" + payload.getAssignedStaffId() + ".";
                type = "SUCCESS";
            }
            case "STATUS_UPDATED" -> {
                title = "Complaint Status Updated";
                message = "Complaint #" + payload.getComplaintId() + " status is now " + payload.getStatus() + ".";
                type = "STATUS_UPDATED".equalsIgnoreCase(payload.getStatus()) ? "SUCCESS" : "INFO";
            }
            case "SLA_BREACHED" -> {
                title = "CRITICAL: SLA Breached!";
                message = "Complaint #" + payload.getComplaintId() + " (" + payload.getTitle() + ") exceeded SLA deadline!";
                type = "URGENT";
            }
            default -> {
                title = "Complaint Notification";
                message = "Update on complaint #" + payload.getComplaintId();
            }
        }

        NotificationDto notification = NotificationDto.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .message(message)
                .type(type)
                .complaintId(payload.getComplaintId())
                .societyId(payload.getSocietyId())
                .status(payload.getStatus())
                .timestamp(Instant.now())
                .build();

        // Keep last 100 in memory
        recentNotifications.add(0, notification);
        if (recentNotifications.size() > 100) {
            recentNotifications.remove(recentNotifications.size() - 1);
        }

        // 1. Push to specific resident channel: /queue/resident-{residentId}
        if (payload.getResidentId() != null) {
            String residentChannel = "/queue/resident-" + payload.getResidentId();
            log.info("Pushing WebSocket alert to resident: {}", residentChannel);
            messagingTemplate.convertAndSend(residentChannel, notification);
        }

        // 2. Push to society admin channel: /topic/society-{societyId}-admin
        if (payload.getSocietyId() != null) {
            String adminChannel = "/topic/society-" + payload.getSocietyId() + "-admin";
            log.info("Pushing WebSocket alert to admin channel: {}", adminChannel);
            messagingTemplate.convertAndSend(adminChannel, notification);
        }

        // 3. Push to assigned staff channel: /queue/staff-{assignedStaffId}
        if (payload.getAssignedStaffId() != null) {
            String staffChannel = "/queue/staff-" + payload.getAssignedStaffId();
            log.info("Pushing WebSocket alert to staff: {}", staffChannel);
            messagingTemplate.convertAndSend(staffChannel, notification);
        }
    }

    public List<NotificationDto> getNotificationsForSociety(Long societyId) {
        return recentNotifications.stream()
                .filter(n -> societyId == null || societyId.equals(n.getSocietyId()))
                .toList();
    }
}
