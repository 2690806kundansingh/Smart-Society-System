package com.smartsociety.user.service;

import com.smartsociety.user.dto.StaffAvailabilityResponse;
import com.smartsociety.user.dto.UserProfileResponse;
import com.smartsociety.user.entity.Department;
import com.smartsociety.user.entity.User;
import com.smartsociety.user.exception.ResourceNotFoundException;
import com.smartsociety.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return authService.mapToProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return authService.mapToProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getStaffMembers(Long societyId, Department department) {
        List<User> staffList;
        if (department != null) {
            staffList = userRepository.findBySocietyIdAndDepartment(societyId, department);
        } else {
            staffList = userRepository.findByRoleNameAndSocietyId("ROLE_STAFF", societyId);
        }

        return staffList.stream()
                .map(authService::mapToProfileResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllStaff() {
        return userRepository.findByRoleName("ROLE_STAFF").stream()
                .map(authService::mapToProfileResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getResidents(Long societyId) {
        return userRepository.findByRoleNameAndSocietyId("ROLE_RESIDENT", societyId).stream()
                .map(authService::mapToProfileResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StaffAvailabilityResponse getStaffAvailability(Long staffId) {
        User user = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + staffId));

        boolean hasStaffRole = user.getRoles().stream()
                .anyMatch(r -> "ROLE_STAFF".equalsIgnoreCase(r.getName()));

        if (!hasStaffRole) {
            throw new IllegalArgumentException("User with ID " + staffId + " does not have the STAFF role");
        }

        return StaffAvailabilityResponse.builder()
                .staffId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .societyId(user.getSocietyId())
                .isAvailable(user.getIsAvailable() != null && user.getIsAvailable() && user.getIsActive() != null && user.getIsActive())
                .activeTicketCount(0L) // Initialized, complaint-service tracks live active tickets
                .build();
    }

    @Transactional
    public void updateStaffAvailability(Long staffId, boolean available) {
        User user = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + staffId));
        user.setIsAvailable(available);
        userRepository.save(user);
        log.info("Updated staffId {} availability to {}", staffId, available);
    }
}
