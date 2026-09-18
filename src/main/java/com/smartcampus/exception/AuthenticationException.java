package com.smartcampus.exception;

/**
 * Thrown when credentials fail or unauthorized access is attempted.
 */
public class AuthenticationException extends CampusException {
    public AuthenticationException(String message) {
        super("AUTH_FAILED", message);
    }
}
