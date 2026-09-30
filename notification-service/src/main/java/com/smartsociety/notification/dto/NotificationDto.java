package com.smartsociety.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto implements Serializable {

    private String id;
    private String title;
    private String message;
    private String type; // INFO, WARNING, SUCCESS, URGENT
    private Long complaintId;
    private String recipientRole;
    private Long targetUserId;
    private Long societyId;
    private String status;
    private Instant timestamp;
}
