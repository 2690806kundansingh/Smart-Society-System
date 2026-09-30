package com.smartsociety.user.dto;

import com.smartsociety.user.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Long societyId;
    private List<String> roles;
    private Department department;
    private String apartment;
    private Boolean isAvailable;
    private Boolean isActive;
}
