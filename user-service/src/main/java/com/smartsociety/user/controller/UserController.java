package com.smartsociety.user.controller;

import com.smartsociety.user.dto.UserProfileResponse;
import com.smartsociety.user.entity.Department;
import com.smartsociety.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserProfile(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<UserProfileResponse>> getStaff(
            @RequestParam(required = false, defaultValue = "1") Long societyId,
            @RequestParam(required = false) Department department) {
        return ResponseEntity.ok(userService.getStaffMembers(societyId, department));
    }

    @GetMapping("/society/{societyId}/residents")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<UserProfileResponse>> getResidents(@PathVariable Long societyId) {
        return ResponseEntity.ok(userService.getResidents(societyId));
    }
}
