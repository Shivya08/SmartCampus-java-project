package com.smartcampus.exception;

/**
 * Thrown when a student attempts to enroll in a course without completing
 * mandatory prerequisite courses.
 */
public class PrerequisiteNotMetException extends CampusException {
    private final String courseCode;
    private final String requiredPrerequisite;

    public PrerequisiteNotMetException(String courseCode, String requiredPrerequisite) {
        super("PREREQUISITE_NOT_MET",
                String.format("Cannot register for course '%s'. Required prerequisite '%s' has not been completed.",
                        courseCode, requiredPrerequisite));
        this.courseCode = courseCode;
        this.requiredPrerequisite = requiredPrerequisite;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getRequiredPrerequisite() {
        return requiredPrerequisite;
    }
}
