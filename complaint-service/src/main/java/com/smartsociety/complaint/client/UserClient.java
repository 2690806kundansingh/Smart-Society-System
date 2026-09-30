package com.smartsociety.complaint.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping("/api/v1/internal/staff/{staffId}")
    StaffAvailabilityDto getStaffAvailability(@PathVariable("staffId") Long staffId);
}
