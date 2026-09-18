package com.smartcampus.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TimeSlot enumeration modeling university slot scheduling (FFCS style).
 * Provides automated conflict/clash detection between scheduled academic sessions.
 */
public enum TimeSlot {
    A1("Theory Slot A1",
            Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            LocalTime.of(8, 0), LocalTime.of(8, 50)),
    B1("Theory Slot B1",
            Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            LocalTime.of(9, 0), LocalTime.of(9, 50)),
    C1("Theory Slot C1",
            Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            LocalTime.of(10, 0), LocalTime.of(10, 50)),
    D1("Theory Slot D1",
            Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            LocalTime.of(11, 0), LocalTime.of(11, 50)),
    E1("Theory Slot E1",
            Arrays.asList(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
            LocalTime.of(8, 0), LocalTime.of(9, 15)),
    F1("Theory Slot F1",
            Arrays.asList(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
            LocalTime.of(9, 30), LocalTime.of(10, 45)),
    L1_L2("Lab Slot L1+L2",
            Collections.singletonList(DayOfWeek.MONDAY),
            LocalTime.of(14, 0), LocalTime.of(15, 40)),
    L3_L4("Lab Slot L3+L4",
            Collections.singletonList(DayOfWeek.TUESDAY),
            LocalTime.of(14, 0), LocalTime.of(15, 40)),
    L5_L6("Lab Slot L5+L6",
            Collections.singletonList(DayOfWeek.WEDNESDAY),
            LocalTime.of(14, 0), LocalTime.of(15, 40)),
    L7_L8("Lab Slot L7+L8",
            Collections.singletonList(DayOfWeek.THURSDAY),
            LocalTime.of(14, 0), LocalTime.of(15, 40));

    private final String description;
    private final List<DayOfWeek> days;
    private final LocalTime startTime;
    private final LocalTime endTime;

    TimeSlot(String description, List<DayOfWeek> days, LocalTime startTime, LocalTime endTime) {
        this.description = description;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public List<DayOfWeek> getDays() {
        return days;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Determines whether this slot clashes with another slot in terms of day and time window.
     */
    public boolean clashesWith(TimeSlot other) {
        if (other == null) return false;
        if (this == other) return true;

        // Check if any scheduled days overlap
        boolean sharesDay = this.days.stream().anyMatch(other.days::contains);
        if (!sharesDay) {
            return false;
        }

        // Check if time windows overlap: (startA < endB) and (endA > startB)
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    public String getFormattedSchedule() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            sb.append(days.get(i).name().substring(0, 3));
            if (i < days.size() - 1) sb.append(", ");
        }
        sb.append(String.format(" %s-%s", startTime, endTime));
        return sb.toString();
    }
}
