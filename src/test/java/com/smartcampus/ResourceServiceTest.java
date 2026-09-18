package com.smartcampus;

import com.smartcampus.exception.CampusException;
import com.smartcampus.exception.ResourceUnavailableException;
import com.smartcampus.model.Reservation;
import com.smartcampus.model.TimeSlot;
import com.smartcampus.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for campus facility reservation and conflict checking.
 */
public class ResourceServiceTest {
    private ResourceService resourceService;

    @BeforeEach
    public void setUp() {
        this.resourceService = new ResourceService();
    }

    @Test
    @DisplayName("Should successfully submit a facility reservation request in PENDING state")
    public void testSuccessfulReservationRequest() throws CampusException {
        LocalDate testDate = LocalDate.now().plusDays(5);
        Reservation res = resourceService.requestReservation("STU201", "RES-POD-01", testDate, TimeSlot.A1, "Team Discussion");

        assertNotNull(res);
        assertEquals(Reservation.Status.PENDING, res.getStatus());
        assertEquals("RES-POD-01", res.getResourceId());
    }

    @Test
    @DisplayName("Should approve reservation and reject conflicting requests for same date and slot")
    public void testConflictDetectionAfterApproval() throws CampusException {
        LocalDate testDate = LocalDate.now().plusDays(10);
        Reservation res1 = resourceService.requestReservation("STU201", "RES-LAB-01", testDate, TimeSlot.C1, "AI Model Training");

        // Admin approves res1
        resourceService.approveReservation("ADM001", res1.getReservationId());
        assertEquals(Reservation.Status.APPROVED, res1.getStatus());

        // Second reservation for identical resource, date, and slot must be blocked
        assertThrows(ResourceUnavailableException.class, () -> {
            resourceService.requestReservation("STU202", "RES-LAB-01", testDate, TimeSlot.C1, "Operating System Practical");
        });
    }
}
