package com.smartcampus.exception;

import com.smartcampus.model.TimeSlot;

import java.time.LocalDate;

/**
 * Thrown when a campus facility is already reserved or unavailable for a requested date and slot.
 */
public class ResourceUnavailableException extends CampusException {
    private final String resourceId;
    private final LocalDate date;
    private final TimeSlot slot;

    public ResourceUnavailableException(String resourceId, LocalDate date, TimeSlot slot, String reason) {
        super("RESOURCE_UNAVAILABLE",
                String.format("Resource '%s' is unavailable on %s for %s. Reason: %s",
                        resourceId, date, slot.name(), reason));
        this.resourceId = resourceId;
        this.date = date;
        this.slot = slot;
    }

    public String getResourceId() {
        return resourceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public TimeSlot getSlot() {
        return slot;
    }
}
