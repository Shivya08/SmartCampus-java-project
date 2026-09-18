package com.smartcampus;

import com.smartcampus.exception.CampusException;
import com.smartcampus.exception.PrerequisiteNotMetException;
import com.smartcampus.exception.ResourceUnavailableException;
import com.smartcampus.exception.SlotClashException;
import com.smartcampus.model.*;
import com.smartcampus.repository.InMemoryDataStore;
import com.smartcampus.service.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Standalone Zero-Dependency Test Suite and Validation Engine.
 * Allows instant verification with standard javac/java without requiring external Maven/JUnit libraries.
 */
public class StandaloneTestRunner {
    private static int passedCount = 0;
    private static int failedCount = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("     SMARTCAMPUS STANDALONE TEST SUITE & VALIDATION RUNNER    ");
        System.out.println("===============================================================\n");

        testRegistrationSuccess();
        testSlotClashDetection();
        testPrerequisiteEnforcement();
        testCourseWithdrawal();
        testResourceReservationAndConflict();
        testBillingAndPaymentCycle();
        testAnalyticsEngine();

        System.out.println("\n---------------------------------------------------------------");
        System.out.printf(" TEST SUMMARY: Total: %d | Passed: %d | Failed: %d%n",
                (passedCount + failedCount), passedCount, failedCount);
        System.out.println("---------------------------------------------------------------");

        if (failedCount > 0) {
            System.err.println("STATUS: FAILED SOME TESTS");
            System.exit(1);
        } else {
            System.out.println("STATUS: ALL TESTS PASSED SUCCESSFULLY! (100% PASS RATE)");
        }
    }

    private static void assertTrue(String testName, boolean condition, String errorMsg) {
        if (condition) {
            System.out.printf("  [PASS] %s%n", testName);
            passedCount++;
        } else {
            System.err.printf("  [FAIL] %s - Reason: %s%n", testName, errorMsg);
            failedCount++;
        }
    }

    private static void testRegistrationSuccess() {
        try {
            InMemoryDataStore ds = InMemoryDataStore.getInstance();
            RegistrationService service = new RegistrationService();
            Student s = (Student) ds.getUsers().get("student");

            Registration reg = service.registerCourse(s.getId(), "CSE2005");
            assertTrue("FFCS Registration Success",
                    reg != null && reg.getStatus() == Registration.Status.REGISTERED && s.getRegisteredCourseCodes().contains("CSE2005"),
                    "Student failed to register for CSE2005");
        } catch (Exception e) {
            assertTrue("FFCS Registration Success", false, e.getMessage());
        }
    }

    private static void testSlotClashDetection() {
        try {
            InMemoryDataStore ds = InMemoryDataStore.getInstance();
            RegistrationService service = new RegistrationService();
            Student s = (Student) ds.getUsers().get("student");

            if (!s.getRegisteredCourseCodes().contains("CSE2005")) {
                service.registerCourse(s.getId(), "CSE2005");
            }

            // Both CSE2005 and CSE3012 are scheduled in Slot B1
            boolean caughtClash = false;
            try {
                service.registerCourse(s.getId(), "CSE3012");
            } catch (SlotClashException e) {
                caughtClash = true;
            }
            assertTrue("Slot Clash Conflict Interception", caughtClash, "Slot clash exception was not thrown");
        } catch (Exception e) {
            assertTrue("Slot Clash Conflict Interception", false, e.getMessage());
        }
    }

    private static void testPrerequisiteEnforcement() {
        try {
            InMemoryDataStore ds = InMemoryDataStore.getInstance();
            RegistrationService service = new RegistrationService();
            Student s = (Student) ds.getUsers().get("student");

            // CSE3003 requires CSE2005 completion (student only currently enrolled, hasn't completed it)
            boolean caughtPrereq = false;
            try {
                service.registerCourse(s.getId(), "CSE3003");
            } catch (PrerequisiteNotMetException e) {
                caughtPrereq = true;
            }
            assertTrue("Mandatory Prerequisite Enforcement", caughtPrereq, "Prerequisite violation was ignored");
        } catch (Exception e) {
            assertTrue("Mandatory Prerequisite Enforcement", false, e.getMessage());
        }
    }

    private static void testCourseWithdrawal() {
        try {
            InMemoryDataStore ds = InMemoryDataStore.getInstance();
            RegistrationService service = new RegistrationService();
            Student s = (Student) ds.getUsers().get("student");

            if (!s.getRegisteredCourseCodes().contains("CSE2005")) {
                service.registerCourse(s.getId(), "CSE2005");
            }

            int beforeCredits = s.getCurrentCredits();
            boolean dropped = service.dropCourse(s.getId(), "CSE2005");
            assertTrue("Course Withdrawal and Credit Recalculation",
                    dropped && !s.getRegisteredCourseCodes().contains("CSE2005") && s.getCurrentCredits() == beforeCredits - 4,
                    "Course withdrawal did not properly clear registered state or adjust credits");
        } catch (Exception e) {
            assertTrue("Course Withdrawal and Credit Recalculation", false, e.getMessage());
        }
    }

    private static void testResourceReservationAndConflict() {
        try {
            ResourceService resService = new ResourceService();
            LocalDate testDate = LocalDate.now().plusDays(7);

            Reservation res1 = resService.requestReservation("STU201", "RES-LAB-01", testDate, TimeSlot.C1, "Operating System Practical");
            assertTrue("Facility Booking Request Creation",
                    res1 != null && res1.getStatus() == Reservation.Status.PENDING,
                    "Reservation not created in PENDING state");

            resService.approveReservation("ADM001", res1.getReservationId());
            assertTrue("Administrator Booking Approval",
                    res1.getStatus() == Reservation.Status.APPROVED,
                    "Reservation status not updated to APPROVED");

            boolean caughtConflict = false;
            try {
                resService.requestReservation("STU202", "RES-LAB-01", testDate, TimeSlot.C1, "Conflicting Lab Session");
            } catch (ResourceUnavailableException e) {
                caughtConflict = true;
            }
            assertTrue("Facility Conflict Detection", caughtConflict, "Overlapping facility reservation was allowed");
        } catch (Exception e) {
            assertTrue("Facility Conflict Detection", false, e.getMessage());
        }
    }

    private static void testBillingAndPaymentCycle() {
        try {
            InMemoryDataStore ds = InMemoryDataStore.getInstance();
            RegistrationService regService = new RegistrationService();
            BillingService billingService = new BillingService();
            Student s = (Student) ds.getUsers().get("student");

            // Ensure student has credits registered
            regService.registerCourse(s.getId(), "CSE2005");
            Invoice inv = billingService.generateSemesterInvoice(s.getId());

            assertTrue("Tuition Fee Invoice Calculation",
                    inv != null && inv.getAmount() > 0 && inv.getStatus() == Invoice.PaymentStatus.UNPAID,
                    "Invoice calculation returned invalid amount or status");

            boolean paid = billingService.payInvoice(s.getId(), inv.getInvoiceId());
            assertTrue("Financial Settlement & Receipt Generation",
                    paid && inv.getStatus() == Invoice.PaymentStatus.PAID && inv.getTransactionReference() != null,
                    "Payment reconciliation failed");
        } catch (Exception e) {
            assertTrue("Financial Settlement & Receipt Generation", false, e.getMessage());
        }
    }

    private static void testAnalyticsEngine() {
        try {
            AnalyticsService analyticsService = new AnalyticsService();
            Map<String, Long> deptDist = analyticsService.getDepartmentalStudentDistribution();
            double avgCgpa = analyticsService.getAverageStudentCgpa();
            String summary = analyticsService.generateExecutiveSummary();

            assertTrue("Analytics: Departmental Aggregations",
                    deptDist != null && !deptDist.isEmpty(), "Departmental distribution empty");
            assertTrue("Analytics: Statistical Average CGPA",
                    avgCgpa > 0.0 && avgCgpa <= 10.0, "Average CGPA outside bounds");
            assertTrue("Analytics: Executive Report Generation",
                    summary != null && summary.contains("SMARTCAMPUS EXECUTIVE ANALYTICS REPORT"), "Summary report missing content");
        } catch (Exception e) {
            assertTrue("Analytics Engine Verification", false, e.getMessage());
        }
    }
}
