package com.smartsociety.user.controller;

import com.smartsociety.user.dto.StaffAvailabilityResponse;
import com.smartsociety.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/staff")
@RequiredArgsConstructor
public class InternalStaffController {

    private final UserService userService;

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
