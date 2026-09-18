package com.smartcampus.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reservation entity representing a scheduled campus facility booking request.
 */
public class Reservation {
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    private final String reservationId;
    private final String userId;
    private final String resourceId;
    private final LocalDate reservationDate;
    private final TimeSlot timeSlot;
    private final String purpose;
    private final LocalDateTime requestedAt;
    private Status status;
    private String adminNotes;

    public Reservation(String reservationId, String userId, String resourceId,
                       LocalDate reservationDate, TimeSlot timeSlot, String purpose) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
        this.requestedAt = LocalDateTime.now();
        this.status = Status.PENDING;
        this.adminNotes = "";
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public String getPurpose() {
        return purpose;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    @Override
    public String toString() {
        return String.format("Booking #%s: Resource %s by User %s on %s Slot %s [%s]",
                reservationId, resourceId, userId, reservationDate, timeSlot.name(), status);
    }
}
