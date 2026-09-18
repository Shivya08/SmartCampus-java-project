package com.smartcampus.model;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Course entity encapsulating academic subject details, capacity, slot allocations,
 * and automated waitlist queue management.
 */
public class Course {
    private final String courseCode;
    private String title;
    private int credits;
    private String department;
    private String facultyId;
    private String facultyName;
    private int maxCapacity;
    private int currentEnrollment;
    private TimeSlot timeSlot;
    private String prerequisiteCode;
    private final Queue<String> waitlist;

    public Course(String courseCode, String title, int credits, String department,
                  String facultyId, String facultyName, int maxCapacity,
                  TimeSlot timeSlot, String prerequisiteCode) {
        this.courseCode = courseCode.toUpperCase().trim();
        this.title = title;
        this.credits = credits;
        this.department = department;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = 0;
        this.timeSlot = timeSlot;
        this.prerequisiteCode = (prerequisiteCode != null && !prerequisiteCode.trim().isEmpty())
                ? prerequisiteCode.toUpperCase().trim() : null;
        this.waitlist = new LinkedList<>();
    }

    public synchronized boolean hasAvailableSeats() {
        return currentEnrollment < maxCapacity;
    }

    public synchronized boolean enroll() {
        if (hasAvailableSeats()) {
            currentEnrollment++;
            return true;
        }
        return false;
    }

    public synchronized boolean withdraw() {
        if (currentEnrollment > 0) {
            currentEnrollment--;
            return true;
        }
        return false;
    }

    public synchronized void addToWaitlist(String studentId) {
        if (!waitlist.contains(studentId)) {
            waitlist.offer(studentId);
        }
    }

    public synchronized String pollWaitlist() {
        return waitlist.poll();
    }

    public synchronized int getWaitlistSize() {
        return waitlist.size();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public synchronized int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getPrerequisiteCode() {
        return prerequisiteCode;
    }

    public void setPrerequisiteCode(String prerequisiteCode) {
        this.prerequisiteCode = prerequisiteCode;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d Credits, Slot: %s, Faculty: %s, Seats: %d/%d)",
                courseCode, title, credits, timeSlot.name(), facultyName, currentEnrollment, maxCapacity);
    }
}
