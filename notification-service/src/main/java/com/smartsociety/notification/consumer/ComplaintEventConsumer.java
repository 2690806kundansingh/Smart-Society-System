package com.smartsociety.notification.consumer;

import com.smartsociety.notification.dispatcher.MockDispatchService;
import com.smartsociety.notification.dispatcher.WebSocketNotificationDispatcher;
import com.smartsociety.notification.event.ComplaintEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class ComplaintEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ComplaintEventConsumer.class);

    private final WebSocketNotificationDispatcher webSocketDispatcher;
    private final MockDispatchService mockDispatchService;

    public ComplaintEventConsumer(WebSocketNotificationDispatcher webSocketDispatcher,
                                  MockDispatchService mockDispatchService) {
        this.webSocketDispatcher = webSocketDispatcher;
        this.mockDispatchService = mockDispatchService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            include = {Exception.class}
    )
    @KafkaListener(
            topics = "society.complaint.events",
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeComplaintEvent(
            ComplaintEventPayload payload,
            Acknowledgment ack,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Kafka consumer received event [{}] for complaint #{} from topic '{}', partition {}, offset {}",
                payload.getEventType(), payload.getComplaintId(), topic, partition, offset);

        try {
            // 1. Dispatch real-time WebSocket toast alerts
            webSocketDispatcher.dispatch(payload);

            // 2. Dispatch mock transactional SMS/Email notifications
            if ("ASSIGNED".equalsIgnoreCase(payload.getEventType())) {
                mockDispatchService.dispatchAssignedAlerts(payload);
            } else if ("SLA_BREACHED".equalsIgnoreCase(payload.getEventType())) {
                mockDispatchService.dispatchSlaBreachAlerts(payload);
            } else if ("STATUS_UPDATED".equalsIgnoreCase(payload.getEventType())) {
                mockDispatchService.dispatchStatusUpdateAlerts(payload);
            }

            // 3. Manual ACK to commit offset
            ack.acknowledge();
            log.info("Committed offset {} for complaint #{}", offset, payload.getComplaintId());

        } catch (Exception ex) {
            log.error("Error processing complaint event #{}: {}", payload.getComplaintId(), ex.getMessage(), ex);
            throw ex; // Re-throw to trigger @RetryableTopic retry pipeline
        }
    }

    @DltHandler
    public void handleDeadLetterTopic(
            ComplaintEventPayload payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.error("==========================================================================");
        log.error("[CRITICAL ALERT] Dead Letter Queue received poison pill message!");
        log.error("DLT Topic: {}, Offset: {}, EventId: {}, ComplaintId: {}",
                topic, offset, payload.getEventId(), payload.getComplaintId());
        log.error("Payload Content: {}", payload);
        log.error("==========================================================================");
    }
}
