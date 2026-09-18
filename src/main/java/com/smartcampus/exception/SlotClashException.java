package com.smartcampus.exception;

import com.smartcampus.model.TimeSlot;

/**
 * Thrown when two courses or bookings have overlapping schedules.
 */
public class SlotClashException extends CampusException {
    private final String courseCode1;
    private final TimeSlot slot1;
    private final String courseCode2;
    private final TimeSlot slot2;

    public SlotClashException(String courseCode1, TimeSlot slot1, String courseCode2, TimeSlot slot2) {
        super("SLOT_CLASH",
                String.format("Timetable Conflict: Course '%s' (%s) clashes with already registered course '%s' (%s).",
                        courseCode1, slot1.getDescription(), courseCode2, slot2.getDescription()));
        this.courseCode1 = courseCode1;
        this.slot1 = slot1;
        this.courseCode2 = courseCode2;
        this.slot2 = slot2;
    }

    public String getCourseCode1() {
        return courseCode1;
    }

    public TimeSlot getSlot1() {
        return slot1;
    }

    public String getCourseCode2() {
        return courseCode2;
    }

    public TimeSlot getSlot2() {
        return slot2;
    }
}
