package com.smartcampus.model;

import java.time.LocalDateTime;

/**
 * Registration entity tracking student enrollment lifecycle in courses.
 */
public class Registration {
    public enum Status {
        REGISTERED,
        WAITLISTED,
        DROPPED
    }

    private final String registrationId;
    private final String studentId;
    private final String courseCode;
    private final LocalDateTime registeredAt;
    private Status status;
    private String grade;

    public Registration(String registrationId, String studentId, String courseCode, Status status) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.courseCode = courseCode.toUpperCase().trim();
        this.registeredAt = LocalDateTime.now();
        this.status = status;
        this.grade = "IP"; // In Progress
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return String.format("Reg #%s: Student %s -> %s [%s] (Grade: %s)",
                registrationId, studentId, courseCode, status, grade);
    }
}
