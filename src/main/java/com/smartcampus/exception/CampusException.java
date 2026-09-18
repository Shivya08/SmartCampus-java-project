package com.smartcampus.exception;

/**
 * Root checked exception for all business and domain-rule violations
 * within the SmartCampus system.
 */
public class CampusException extends Exception {
    private final String errorCode;

    public CampusException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CampusException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
