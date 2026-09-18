package com.smartcampus.model;

/**
 * Role enumeration defining system authorization levels for RBAC.
 */
public enum Role {
    STUDENT,
    FACULTY,
    ADMIN;

    public String getDisplayName() {
        switch (this) {
            case STUDENT:
                return "Student";
            case FACULTY:
                return "Faculty Member";
            case ADMIN:
                return "Academic Administrator";
            default:
                return name();
        }
    }
}
