package com.smartcampus.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Faculty member model representing teaching and research staff.
 * Demonstrates Inheritance and Domain Specialization.
 */
public class Faculty extends User {
    private final String employeeId;
    private String department;
    private String designation;
    private final Set<String> assignedCourseCodes;

    public Faculty(String id, String username, String plainPassword, String fullName, String email,
                   String employeeId, String department, String designation) {
        super(id, username, plainPassword, fullName, email, Role.FACULTY);
        this.employeeId = employeeId;
        this.department = department;
        this.designation = designation;
        this.assignedCourseCodes = new HashSet<>();
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Faculty: %s (%s) | Dept: %s | Courses Handled: %d",
                getFullName(), designation, department, assignedCourseCodes.size());
    }

    public void assignCourse(String courseCode) {
        if (courseCode != null && !courseCode.trim().isEmpty()) {
            assignedCourseCodes.add(courseCode.toUpperCase().trim());
        }
    }

    public void removeCourse(String courseCode) {
        if (courseCode != null) {
            assignedCourseCodes.remove(courseCode.toUpperCase().trim());
        }
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Set<String> getAssignedCourseCodes() {
        return Collections.unmodifiableSet(assignedCourseCodes);
    }
}
