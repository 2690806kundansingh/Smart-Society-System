package com.smartsociety.complaint.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintEventProducer {

    private static final String TOPIC = "society.complaint.events";
    private final KafkaTemplate<String, ComplaintEventPayload> kafkaTemplate;

    public void publishEvent(ComplaintEventPayload payload) {
        if (payload.getEventId() == null) {
            payload.setEventId(UUID.randomUUID().toString());
        }
        String partitionKey = String.valueOf(payload.getComplaintId());

        log.info("Emitting Kafka event [{}] for complaintId: {} to topic: {}",
                payload.getEventType(), payload.getComplaintId(), TOPIC);

        kafkaTemplate.send(TOPIC, partitionKey, payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka event [{}] delivered successfully with offset: {}",
                                payload.getEventType(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish Kafka event [{}]: {}", payload.getEventType(), ex.getMessage(), ex);
                    }
                });
    }
}
