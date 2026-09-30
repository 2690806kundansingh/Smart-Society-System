package com.smartsociety.complaint.scheduler;

import com.smartsociety.complaint.entity.Complaint;
import com.smartsociety.complaint.entity.ComplaintAuditLog;
import com.smartsociety.complaint.entity.ComplaintStatus;
import com.smartsociety.complaint.event.ComplaintEventPayload;
import com.smartsociety.complaint.event.ComplaintEventProducer;
import com.smartsociety.complaint.event.ComplaintEventType;
import com.smartsociety.complaint.repository.ComplaintAuditLogRepository;
import com.smartsociety.complaint.repository.ComplaintRepository;
import com.smartsociety.complaint.service.DashboardStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlaEscalationScheduler {

    private final ComplaintRepository complaintRepository;
    private final ComplaintAuditLogRepository auditLogRepository;
    private final ComplaintEventProducer eventProducer;
    private final DashboardStatsService dashboardStatsService;

    private static final List<ComplaintStatus> TERMINAL_STATUSES = List.of(
            ComplaintStatus.RESOLVED,
            ComplaintStatus.CLOSED
    );

    /**
     * SLA Watcher running periodically with distributed ShedLock to prevent duplicate execution across instances.
     */
    @Scheduled(fixedDelayString = "${sla.watcher.interval-ms:60000}")
    @SchedulerLock(name = "slaEscalationTask", lockAtLeastFor = "30s", lockAtMostFor = "2m")
    @Transactional
    public void scanAndEscalateBreachedComplaints() {
        Instant now = Instant.now();
        log.info("SLA Watcher triggered at {}. Scanning for overdue active complaints...", now);

        List<Complaint> breachedComplaints = complaintRepository
                .findBySlaDeadlineBeforeAndSlaBreachedFalseAndStatusNotIn(now, TERMINAL_STATUSES);

        if (breachedComplaints.isEmpty()) {
            log.info("SLA Watcher scan completed: 0 overdue complaints found.");
            return;
        }

        log.warn("SLA Watcher detected {} complaints breaching SLA threshold!", breachedComplaints.size());

        for (Complaint complaint : breachedComplaints) {
            complaint.setSlaBreached(true);
            complaint.setEscalated(true);
            complaintRepository.save(complaint);

            // Record SLA Breach in Audit Log
            ComplaintAuditLog auditLog = ComplaintAuditLog.builder()
                    .complaintId(complaint.getId())
                    .action("SLA_BREACHED")
                    .performedByRole("SYSTEM_SLA_WATCHER")
                    .previousStatus(complaint.getStatus())
                    .newStatus(complaint.getStatus())
                    .notes("Automated SLA escalation: Deadline (" + complaint.getSlaDeadline() + ") expired before resolution.")
                    .build();
            auditLogRepository.save(auditLog);

            // Emit SLA_BREACHED event to Kafka topic
            ComplaintEventPayload event = ComplaintEventPayload.builder()
                    .eventType(ComplaintEventType.SLA_BREACHED)
                    .complaintId(complaint.getId())
                    .title(complaint.getTitle())
                    .societyId(complaint.getSocietyId())
                    .residentId(complaint.getResidentId())
                    .assignedStaffId(complaint.getAssignedStaffId())
                    .status(complaint.getStatus().name())
                    .priority(complaint.getPriority().name())
                    .slaDeadline(complaint.getSlaDeadline())
                    .slaBreached(true)
                    .timestamp(Instant.now())
                    .build();
            eventProducer.publishEvent(event);

            // Invalidate cache for the society
            dashboardStatsService.evictStatsCache(complaint.getSocietyId());

            log.warn("Complaint #{} ({}) flagged as SLA_BREACHED and dispatched to Kafka.",
                    complaint.getId(), complaint.getTitle());
        }
    }
}
