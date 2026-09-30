package com.smartsociety.complaint.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffAvailabilityDto {
    private Long staffId;
    private String fullName;
    private String email;
    private String department;
    private Long societyId;
    private Boolean isAvailable;
    private Long activeTicketCount;
}
