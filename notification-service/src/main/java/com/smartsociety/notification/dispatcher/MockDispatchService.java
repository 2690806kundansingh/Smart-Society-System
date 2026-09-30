package com.smartsociety.notification.dispatcher;

import com.smartsociety.notification.event.ComplaintEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockDispatchService {

    public void dispatchAssignedAlerts(ComplaintEventPayload payload) {
        log.info("========================================================================");
        log.info("[MOCK-SMS] Recipient: Staff #{}, Subject: Ticket Assigned", payload.getAssignedStaffId());
        log.info("[MOCK-SMS] Message: 'New ticket #{} (Priority: {}) assigned: \"{}\". Please review and accept immediately in your staff portal.'",
                payload.getComplaintId(), payload.getPriority(), payload.getTitle());
        log.info("[MOCK-EMAIL] Recipient: resident{}@smartsociety.com", payload.getResidentId());
        log.info("[MOCK-EMAIL] Subject: Your Complaint #{} has been assigned", payload.getComplaintId());
        log.info("[MOCK-EMAIL] Body: 'Hello! Your complaint \"{}\" has been assigned to a maintenance engineer. SLA Target: {}.'",
                payload.getTitle(), payload.getSlaDeadline());
        log.info("========================================================================");
    }

    public void dispatchSlaBreachAlerts(ComplaintEventPayload payload) {
        log.error("************************************************************************");
        log.error("[MOCK-SMS] URGENT ESCALATION ALERT -> Society #{} Admin (+91-9876543210)", payload.getSocietyId());
        log.error("[MOCK-SMS] Message: 'CRITICAL: Complaint #{} (\"{}\") has BREACHED SLA! Immediate supervisor intervention required.'",
                payload.getComplaintId(), payload.getTitle());
        log.error("[MOCK-EMAIL] URGENT -> admin@smartsociety.com");
        log.error("[MOCK-EMAIL] Subject: [SLA BREACH ALERT] Complaint #{} Breached Deadline", payload.getComplaintId());
        log.error("[MOCK-EMAIL] Body: 'Complaint #{} titled \"{}\" (Priority: {}) exceeded SLA deadline {}. Current status: {}.'",
                payload.getComplaintId(), payload.getTitle(), payload.getPriority(), payload.getSlaDeadline(), payload.getStatus());
        log.error("************************************************************************");
    }

    public void dispatchStatusUpdateAlerts(ComplaintEventPayload payload) {
        log.info("------------------------------------------------------------------------");
        log.info("[MOCK-EMAIL] Recipient: resident{}@smartsociety.com", payload.getResidentId());
        log.info("[MOCK-EMAIL] Subject: Complaint #{} Status Update: {}", payload.getComplaintId(), payload.getStatus());
        log.info("[MOCK-EMAIL] Body: 'Your complaint \"{}\" status changed to {}.'", payload.getTitle(), payload.getStatus());
        log.info("------------------------------------------------------------------------");
    }
}
