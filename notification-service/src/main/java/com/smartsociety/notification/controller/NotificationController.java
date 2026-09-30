package com.smartsociety.notification.controller;

import com.smartsociety.notification.dispatcher.WebSocketNotificationDispatcher;
import com.smartsociety.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final WebSocketNotificationDispatcher dispatcher;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @RequestParam(required = false) Long societyId) {
        return ResponseEntity.ok(dispatcher.getNotificationsForSociety(societyId));
    }
}
