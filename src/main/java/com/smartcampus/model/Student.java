package com.smartcampus.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Student entity representing an enrolled learner in the university.
 * Demonstrates Inheritance and State Management.
 */
public class Student extends User {
    private final String rollNumber;
    private String department;
    private int semester;
    private double cgpa;
    private final Set<String> completedCourseCodes;
    private final Set<String> registeredCourseCodes;
    private int currentCredits;

    public static final int MIN_CREDIT_LIMIT = 16;
    public static final int MAX_CREDIT_LIMIT = 27;

    public Student(String id, String username, String plainPassword, String fullName, String email,
                   String rollNumber, String department, int semester, double cgpa) {
        super(id, username, plainPassword, fullName, email, Role.STUDENT);
        this.rollNumber = rollNumber;
        this.department = department;
        this.semester = semester;
        this.cgpa = cgpa;
        this.completedCourseCodes = new HashSet<>();
        this.registeredCourseCodes = new HashSet<>();
        this.currentCredits = 0;
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Student: %s | Roll No: %s | Dept: %s | Sem: %d | CGPA: %.2f | Registered Credits: %d/%d",
                getFullName(), rollNumber, department, semester, cgpa, currentCredits, MAX_CREDIT_LIMIT);
    }

    public void addCompletedCourse(String courseCode) {
        if (courseCode != null && !courseCode.trim().isEmpty()) {
            this.completedCourseCodes.add(courseCode.toUpperCase().trim());
        }
    }

    public boolean hasCompletedPrerequisite(String prerequisiteCode) {
        if (prerequisiteCode == null || prerequisiteCode.trim().isEmpty()) {
            return true;
        }
        return completedCourseCodes.contains(prerequisiteCode.toUpperCase().trim());
    }

    public void registerCourse(String courseCode, int credits) {
        registeredCourseCodes.add(courseCode.toUpperCase().trim());
        this.currentCredits += credits;
    }

    public void dropCourse(String courseCode, int credits) {
        if (registeredCourseCodes.remove(courseCode.toUpperCase().trim())) {
            this.currentCredits = Math.max(0, this.currentCredits - credits);
        }
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public Set<String> getCompletedCourseCodes() {
        return Collections.unmodifiableSet(completedCourseCodes);
    }

    public Set<String> getRegisteredCourseCodes() {
        return Collections.unmodifiableSet(registeredCourseCodes);
    }

    public int getCurrentCredits() {
        return currentCredits;
    }
}
