package com.smartsociety.complaint.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    private static final Logger log = LoggerFactory.getLogger(UserClientFallbackFactory.class);

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
