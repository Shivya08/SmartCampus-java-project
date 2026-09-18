package com.smartcampus.service;

import com.smartcampus.concurrency.AsyncAuditLogger;
import com.smartcampus.exception.*;
import com.smartcampus.model.*;
import com.smartcampus.repository.InMemoryDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core Course Registration Engine implementing VIT's Fully Flexible Credit System (FFCS) logic.
 * Handles timetable conflict / slot clash detection, prerequisite validation, credit limits,
 * and automated waitlist promotion.
 */
public class RegistrationService {
    private final InMemoryDataStore dataStore;
    private final AsyncAuditLogger logger;

    public RegistrationService() {
        this.dataStore = InMemoryDataStore.getInstance();
        this.logger = AsyncAuditLogger.getInstance();
    }

    /**
     * Registers a student for a target course after exhaustive academic validation.
     */
    public synchronized Registration registerCourse(String studentId, String courseCode)
            throws CampusException {

        Student student = findStudentById(studentId);
        Course targetCourse = findCourseByCode(courseCode);

        // 1. Check if student already registered for this course
        if (student.getRegisteredCourseCodes().contains(targetCourse.getCourseCode())) {
            throw new CampusException("ALREADY_REGISTERED",
                    String.format("Student %s is already enrolled in course %s.", student.getRollNumber(), targetCourse.getCourseCode()));
        }

        // 2. Prerequisite Validation
        if (targetCourse.getPrerequisiteCode() != null) {
            if (!student.hasCompletedPrerequisite(targetCourse.getPrerequisiteCode())) {
                logger.logWarning(studentId, "REGISTRATION_REJECTED",
                        "Prerequisite not met: requires " + targetCourse.getPrerequisiteCode());
                throw new PrerequisiteNotMetException(targetCourse.getCourseCode(), targetCourse.getPrerequisiteCode());
            }
        }

        // 3. Credit Limit Validation (Max 27 credits per semester)
        int projectedCredits = student.getCurrentCredits() + targetCourse.getCredits();
        if (projectedCredits > Student.MAX_CREDIT_LIMIT) {
            logger.logWarning(studentId, "CREDIT_LIMIT_BLOCKED",
                    String.format("Exceeds cap: Current %d + Course %d > %d",
                            student.getCurrentCredits(), targetCourse.getCredits(), Student.MAX_CREDIT_LIMIT));
            throw new CreditLimitExceededException(student.getCurrentCredits(), targetCourse.getCredits(), Student.MAX_CREDIT_LIMIT);
        }

        // 4. Timetable Slot Clash Detection
        for (String registeredCode : student.getRegisteredCourseCodes()) {
            Course registeredCourse = dataStore.getCourses().get(registeredCode);
            if (registeredCourse != null && targetCourse.getTimeSlot().clashesWith(registeredCourse.getTimeSlot())) {
                logger.logWarning(studentId, "SLOT_CLASH_DETECTED",
                        String.format("Conflict between %s (%s) and %s (%s)",
                                targetCourse.getCourseCode(), targetCourse.getTimeSlot(),
                                registeredCourse.getCourseCode(), registeredCourse.getTimeSlot()));
                throw new SlotClashException(targetCourse.getCourseCode(), targetCourse.getTimeSlot(),
                        registeredCourse.getCourseCode(), registeredCourse.getTimeSlot());
            }
        }

        // 5. Seat Allocation & Waitlist Handling
        String regId = dataStore.nextRegistrationId();
        Registration registration;

        if (targetCourse.hasAvailableSeats()) {
            targetCourse.enroll();
            student.registerCourse(targetCourse.getCourseCode(), targetCourse.getCredits());
            registration = new Registration(regId, student.getId(), targetCourse.getCourseCode(), Registration.Status.REGISTERED);
            dataStore.getRegistrations().put(regId, registration);

            logger.logInfo(studentId, "COURSE_REGISTERED",
                    String.format("Successfully enrolled in %s (%s). Total credits: %d",
                            targetCourse.getCourseCode(), targetCourse.getTitle(), student.getCurrentCredits()));
        } else {
            targetCourse.addToWaitlist(student.getId());
            registration = new Registration(regId, student.getId(), targetCourse.getCourseCode(), Registration.Status.WAITLISTED);
            dataStore.getRegistrations().put(regId, registration);

            logger.logInfo(studentId, "COURSE_WAITLISTED",
                    String.format("Course %s is full. Added to waitlist position #%d",
                            targetCourse.getCourseCode(), targetCourse.getWaitlistSize()));
        }

        return registration;
    }

    /**
     * Drops an enrolled course and automatically promotes the next student in the waitlist if present.
     */
    public synchronized boolean dropCourse(String studentId, String courseCode) throws CampusException {
        Student student = findStudentById(studentId);
        Course course = findCourseByCode(courseCode);

        if (!student.getRegisteredCourseCodes().contains(course.getCourseCode())) {
            throw new CampusException("NOT_REGISTERED",
                    String.format("Student %s is not currently registered in %s", student.getRollNumber(), courseCode));
        }

        // Drop from student and decrement course enrollment
        student.dropCourse(course.getCourseCode(), course.getCredits());
        course.withdraw();

        // Update registration record status
        for (Registration reg : dataStore.getRegistrations().values()) {
            if (reg.getStudentId().equals(student.getId()) &&
                    reg.getCourseCode().equalsIgnoreCase(course.getCourseCode()) &&
                    reg.getStatus() == Registration.Status.REGISTERED) {
                reg.setStatus(Registration.Status.DROPPED);
                break;
            }
        }

        logger.logInfo(studentId, "COURSE_DROPPED",
                String.format("Dropped course %s. Current credits: %d", course.getCourseCode(), student.getCurrentCredits()));

        // Check if there is a waitlisted student to auto-promote
        String nextStudentId = course.pollWaitlist();
        if (nextStudentId != null) {
            try {
                Student waitlistedStudent = findStudentById(nextStudentId);
                course.enroll();
                waitlistedStudent.registerCourse(course.getCourseCode(), course.getCredits());

                for (Registration reg : dataStore.getRegistrations().values()) {
                    if (reg.getStudentId().equals(waitlistedStudent.getId()) &&
                            reg.getCourseCode().equalsIgnoreCase(course.getCourseCode()) &&
                            reg.getStatus() == Registration.Status.WAITLISTED) {
                        reg.setStatus(Registration.Status.REGISTERED);
                        break;
                    }
                }

                logger.logInfo(waitlistedStudent.getId(), "WAITLIST_PROMOTED",
                        String.format("Seat opened! Auto-promoted to REGISTERED for %s", course.getCourseCode()));
            } catch (Exception e) {
                logger.logError("REGISTRATION_ENGINE", "WAITLIST_PROMOTION_ERROR", e.getMessage());
            }
        }

        return true;
    }

    public List<Course> getStudentRegisteredCourses(String studentId) throws CampusException {
        Student student = findStudentById(studentId);
        return student.getRegisteredCourseCodes().stream()
                .map(code -> dataStore.getCourses().get(code))
                .filter(course -> course != null)
                .collect(Collectors.toList());
    }

    public List<Registration> getStudentRegistrations(String studentId) {
        return dataStore.getRegistrations().values().stream()
                .filter(reg -> reg.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    private Student findStudentById(String studentId) throws EntityNotFoundException {
        for (User user : dataStore.getUsers().values()) {
            if (user instanceof Student && (user.getId().equalsIgnoreCase(studentId) || user.getUsername().equalsIgnoreCase(studentId))) {
                return (Student) user;
            }
        }
        throw new EntityNotFoundException("Student", studentId);
    }

    private Course findCourseByCode(String courseCode) throws EntityNotFoundException {
        Course course = dataStore.getCourses().get(courseCode.toUpperCase().trim());
        if (course == null) {
            throw new EntityNotFoundException("Course", courseCode);
        }
        return course;
    }
}
