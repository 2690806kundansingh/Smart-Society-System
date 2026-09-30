package com.smartsociety.complaint.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public StaffAvailabilityDto getStaffAvailability(Long staffId) {
                log.warn("Resilience4j CircuitBreaker fallback triggered for staffId: {}. Reason: {}",
                        staffId, cause.getMessage());
                // In fallback mode when user-service is degraded, return safe default
                return StaffAvailabilityDto.builder()
                        .staffId(staffId)
                        .fullName("Staff Member #" + staffId)
                        .department("GENERAL")
                        .isAvailable(true) // Allow graceful degradation or fallback policy
                        .activeTicketCount(0L)
                        .build();
            }
        };
    }
}
