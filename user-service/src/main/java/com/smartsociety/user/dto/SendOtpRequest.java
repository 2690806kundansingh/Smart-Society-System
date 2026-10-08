package com.smartsociety.user.dto;

import jakarta.validation.constraints.NotBlank;

public class SendOtpRequest {

    @NotBlank(message = "Identifier (Email or Phone Number) is required")
    private String identifier;

    private String type; // "PHONE" or "EMAIL"

    public SendOtpRequest() {}

    public SendOtpRequest(String identifier, String type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
