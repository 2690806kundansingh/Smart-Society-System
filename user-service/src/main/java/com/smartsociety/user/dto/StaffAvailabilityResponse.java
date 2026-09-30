package com.smartsociety.user.dto;

import com.smartsociety.user.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffAvailabilityResponse {

    private Long staffId;
    private String fullName;
    private String email;
    private Department department;
    private Long societyId;
    private Boolean isAvailable;
    private Long activeTicketCount;
}
