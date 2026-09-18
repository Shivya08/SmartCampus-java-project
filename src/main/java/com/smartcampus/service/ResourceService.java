package com.smartcampus.service;

import com.smartcampus.concurrency.AsyncAuditLogger;
import com.smartcampus.exception.CampusException;
import com.smartcampus.exception.EntityNotFoundException;
import com.smartcampus.exception.ResourceUnavailableException;
import com.smartcampus.model.Reservation;
import com.smartcampus.model.Resource;
import com.smartcampus.model.TimeSlot;
import com.smartcampus.repository.InMemoryDataStore;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource and Facility Reservation Management Service.
 * Coordinates campus infrastructure booking, room capacity, conflict resolution,
 * and administrative approvals.
 */
public class ResourceService {
    private final InMemoryDataStore dataStore;
    private final AsyncAuditLogger logger;

    public ResourceService() {
        this.dataStore = InMemoryDataStore.getInstance();
        this.logger = AsyncAuditLogger.getInstance();
    }

    public synchronized Reservation requestReservation(String userId, String resourceId,
                                                        LocalDate date, TimeSlot slot, String purpose)
            throws CampusException {

        Resource resource = dataStore.getResources().get(resourceId);
        if (resource == null) {
            throw new EntityNotFoundException("Campus Resource", resourceId);
        }

        if (!resource.isAvailable()) {
            throw new ResourceUnavailableException(resourceId, date, slot, "Facility is under maintenance.");
        }

        // Check for conflicting approved reservations
        boolean isConflict = dataStore.getReservations().values().stream()
                .anyMatch(r -> r.getResourceId().equalsIgnoreCase(resourceId) &&
                        r.getReservationDate().equals(date) &&
                        r.getTimeSlot() == slot &&
                        r.getStatus() == Reservation.Status.APPROVED);

        if (isConflict) {
            logger.logWarning(userId, "RESERVATION_CONFLICT",
                    String.format("Resource %s is already reserved on %s for slot %s", resourceId, date, slot));
            throw new ResourceUnavailableException(resourceId, date, slot, "Slot already booked by another entity.");
        }

        String resId = dataStore.nextReservationId();
        Reservation reservation = new Reservation(resId, userId, resourceId, date, slot, purpose);
        dataStore.getReservations().put(resId, reservation);

        logger.logInfo(userId, "RESERVATION_REQUESTED",
                String.format("Requested booking for %s on %s slot %s", resource.getName(), date, slot));

        return reservation;
    }

    public synchronized void approveReservation(String adminId, String reservationId) throws CampusException {
        Reservation res = dataStore.getReservations().get(reservationId);
        if (res == null) {
            throw new EntityNotFoundException("Reservation", reservationId);
        }

        // Re-verify no other reservation was approved in the interim
        boolean isConflict = dataStore.getReservations().values().stream()
                .anyMatch(r -> !r.getReservationId().equals(res.getReservationId()) &&
                        r.getResourceId().equalsIgnoreCase(res.getResourceId()) &&
                        r.getReservationDate().equals(res.getReservationDate()) &&
                        r.getTimeSlot() == res.getTimeSlot() &&
                        r.getStatus() == Reservation.Status.APPROVED);

        if (isConflict) {
            res.setStatus(Reservation.Status.REJECTED);
            res.setAdminNotes("Auto-rejected: another booking was approved earlier for this timeslot.");
            throw new ResourceUnavailableException(res.getResourceId(), res.getReservationDate(), res.getTimeSlot(),
                    "Concurrent approval conflict detected.");
        }

        res.setStatus(Reservation.Status.APPROVED);
        res.setAdminNotes("Approved by administrator " + adminId);
        logger.logInfo(adminId, "RESERVATION_APPROVED", "Approved booking " + reservationId);
    }

    public synchronized void rejectReservation(String adminId, String reservationId, String reason)
            throws CampusException {
        Reservation res = dataStore.getReservations().get(reservationId);
        if (res == null) {
            throw new EntityNotFoundException("Reservation", reservationId);
        }
        res.setStatus(Reservation.Status.REJECTED);
        res.setAdminNotes("Rejected by " + adminId + ": " + reason);
        logger.logInfo(adminId, "RESERVATION_REJECTED", "Rejected booking " + reservationId + " (" + reason + ")");
    }

    public List<Reservation> getUserReservations(String userId) {
        return dataStore.getReservations().values().stream()
                .filter(r -> r.getUserId().equalsIgnoreCase(userId))
                .collect(Collectors.toList());
    }

    public List<Reservation> getAllReservations() {
        return List.copyOf(dataStore.getReservations().values());
    }

    public List<Resource> getAllResources() {
        return List.copyOf(dataStore.getResources().values());
    }
}
