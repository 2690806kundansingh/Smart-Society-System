package com.smartsociety.user.dto;

public class OtpResponse {

    private String message;
    private String identifier;
    private boolean success;
    private String debugOtp;

    public OtpResponse() {}

    public OtpResponse(String message, String identifier, boolean success, String debugOtp) {
        this.message = message;
        this.identifier = identifier;
        this.success = success;
        this.debugOtp = debugOtp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDebugOtp() {
        return debugOtp;
    }

    public void setDebugOtp(String debugOtp) {
        this.debugOtp = debugOtp;
    }
}
