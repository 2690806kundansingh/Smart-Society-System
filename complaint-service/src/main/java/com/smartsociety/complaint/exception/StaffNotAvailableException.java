package com.smartsociety.complaint.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StaffNotAvailableException extends RuntimeException {
    public StaffNotAvailableException(String message) {
        super(message);
    }
}
