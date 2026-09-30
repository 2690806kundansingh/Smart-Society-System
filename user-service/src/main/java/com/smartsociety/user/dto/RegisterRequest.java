package com.smartsociety.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phoneNumber;

    @NotNull(message = "Society ID is required")
    private Long societyId;

    private String role; // ROLE_RESIDENT, ROLE_STAFF, ROLE_ADMIN (defaults to ROLE_RESIDENT)

    private String blockName;

    private String flatNumber;

    private String department; // For staff (PLUMBING, ELECTRICAL, etc.)
}
