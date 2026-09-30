package com.smartsociety.user.controller;

import com.smartsociety.user.dto.StaffAvailabilityResponse;
import com.smartsociety.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/staff")
public class InternalStaffController {

    private static final Logger log = LoggerFactory.getLogger(InternalStaffController.class);

    private final UserService userService;

    public InternalStaffController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<StaffAvailabilityResponse> getStaffAvailability(@PathVariable Long staffId) {
        log.info("Inter-service call: fetching availability for staffId: {}", staffId);
        return ResponseEntity.ok(userService.getStaffAvailability(staffId));
    }

    @PutMapping("/{staffId}/availability")
    public ResponseEntity<Map<String, Object>> updateAvailability(
            @PathVariable Long staffId,
            @RequestParam boolean available) {
        log.info("Inter-service call: updating availability for staffId: {} to {}", staffId, available);
        userService.updateStaffAvailability(staffId, available);
        return ResponseEntity.ok(Map.of("staffId", staffId, "available", available, "status", "SUCCESS"));
    }
}
