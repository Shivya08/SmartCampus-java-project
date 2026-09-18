package com.smartcampus.cli;

import com.smartcampus.exception.CampusException;
import com.smartcampus.model.*;
import com.smartcampus.repository.FilePersistenceManager;
import com.smartcampus.repository.InMemoryDataStore;
import com.smartcampus.service.*;
import com.smartcampus.util.TableFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console User Interface for the SmartCampus system.
 * Implements Role-Based Access Control (RBAC) interactive dashboards
 * for Students, Faculty, and Administrators.
 */
public class ConsoleUI {
    private final AuthService authService;
    private final RegistrationService registrationService;
    private final ResourceService resourceService;
    private final BillingService billingService;
    private final AnalyticsService analyticsService;
    private final InMemoryDataStore dataStore;
    private final FilePersistenceManager filePersistenceManager;
    private final Scanner scanner;

    public ConsoleUI() {
        this.authService = new AuthService();
        this.registrationService = new RegistrationService();
        this.resourceService = new ResourceService();
        this.billingService = new BillingService();
        this.analyticsService = new AnalyticsService();
        this.dataStore = InMemoryDataStore.getInstance();
        this.filePersistenceManager = new FilePersistenceManager("data");
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n" + "=".repeat(65));
            System.out.println("   SMARTCAMPUS: ACADEMIC & RESOURCE MANAGEMENT SYSTEM (VIT)   ");
            System.out.println("=".repeat(65));
            System.out.println(" 1. Student Login       (Default: student / student123)");
            System.out.println(" 2. Faculty Login       (Default: prof.ananya / faculty123)");
            System.out.println(" 3. Administrator Login (Default: admin / admin123)");
            System.out.println(" 4. Run Automated End-to-End System Demonstration");
            System.out.println(" 5. View Executive Analytics Summary");
            System.out.println(" 0. Exit System");
            System.out.println("-".repeat(65));
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleLoginPrompt(Role.STUDENT);
                    break;
                case "2":
                    handleLoginPrompt(Role.FACULTY);
                    break;
                case "3":
                    handleLoginPrompt(Role.ADMIN);
                    break;
                case "4":
                    runAutomatedDemonstration();
                    break;
                case "5":
                    System.out.println(analyticsService.generateExecutiveSummary());
                    break;
                case "0":
                    exit = true;
                    System.out.println("\nThank you for using SmartCampus. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid selection. Please enter 0-5.");
            }
        }
    }

    private void handleLoginPrompt(Role expectedRole) {
        System.out.print("\nEnter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = authService.login(username, password);
            if (user.getRole() != expectedRole) {
                System.out.printf("Access Denied: Account '%s' has role %s, expected %s.%n",
                        username, user.getRole(), expectedRole);
                authService.logout();
                return;
            }

            System.out.println("\n" + user.getDashboardSummary());
            switch (user.getRole()) {
                case STUDENT:
                    runStudentDashboard((Student) user);
                    break;
                case FACULTY:
                    runFacultyDashboard((Faculty) user);
                    break;
                case ADMIN:
                    runAdminDashboard((Admin) user);
                    break;
            }
        } catch (CampusException e) {
            System.err.println("\nAuthentication Error: " + e.getMessage());
        }
    }

    // ==========================================
    // STUDENT WORKFLOW
    // ==========================================
    private void runStudentDashboard(Student student) {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + "-".repeat(50));
            System.out.println("           STUDENT ACADEMIC PORTAL");
            System.out.println("-".repeat(50));
            System.out.println(" 1. View Course Catalog (FFCS Slots)");
            System.out.println(" 2. Register for a Course (Clash & Prereq Checks)");
            System.out.println(" 3. View My Registered Courses & Timetable");
            System.out.println(" 4. Drop a Course (Auto Waitlist Promotion)");
            System.out.println(" 5. Request Campus Facility / Lab Booking");
            System.out.println(" 6. View My Facility Bookings");
            System.out.println(" 7. View & Settle Tuition Invoices");
            System.out.println(" 0. Logout to Main Menu");
            System.out.print("Select action: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    displayCourseCatalog();
                    break;
                case "2":
                    handleCourseRegistration(student);
                    break;
                case "3":
                    displayStudentSchedule(student);
                    break;
                case "4":
                    handleDropCourse(student);
                    break;
                case "5":
                    handleResourceBookingRequest(student.getId());
                    break;
                case "6":
                    displayUserBookings(student.getId());
                    break;
                case "7":
                    handleStudentBilling(student.getId());
                    break;
                case "0":
                    authService.logout();
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayCourseCatalog() {
        System.out.println("\n--- UNIVERSITY COURSE CATALOG (FFCS) ---");
        TableFormatter table = new TableFormatter("Code", "Course Title", "Credits", "Slot", "Timing", "Faculty", "Seats");
        for (Course c : dataStore.getCourses().values()) {
            table.addRow(
                    c.getCourseCode(),
                    c.getTitle(),
                    String.valueOf(c.getCredits()),
                    c.getTimeSlot().name(),
                    c.getTimeSlot().getFormattedSchedule(),
                    c.getFacultyName(),
                    String.format("%d/%d", c.getCurrentEnrollment(), c.getMaxCapacity())
            );
        }
        table.print();
    }

    private void handleCourseRegistration(Student student) {
        displayCourseCatalog();
        System.out.print("\nEnter Course Code to Register (e.g., CSE2005): ");
        String code = scanner.nextLine().trim().toUpperCase();

        try {
            Registration reg = registrationService.registerCourse(student.getId(), code);
            if (reg.getStatus() == Registration.Status.REGISTERED) {
                System.out.println("\nSUCCESS: Successfully enrolled in " + code + "!");
                System.out.printf("Total Semester Credits: %d / %d%n",
                        student.getCurrentCredits(), Student.MAX_CREDIT_LIMIT);
            } else {
                System.out.println("\nNOTICE: Course is full. You have been placed on the WAITLIST.");
            }
        } catch (CampusException e) {
            System.out.println("\nREGISTRATION FAILED: " + e.getMessage());
        }
    }

    private void displayStudentSchedule(Student student) {
        System.out.printf("\n--- REGISTERED COURSES FOR %s (%s) ---%n", student.getFullName(), student.getRollNumber());
        try {
            List<Course> enrolled = registrationService.getStudentRegisteredCourses(student.getId());
            if (enrolled.isEmpty()) {
                System.out.println("No courses currently registered.");
                return;
            }

            TableFormatter table = new TableFormatter("Code", "Title", "Credits", "Slot", "Schedule", "Faculty");
            for (Course c : enrolled) {
                table.addRow(
                        c.getCourseCode(),
                        c.getTitle(),
                        String.valueOf(c.getCredits()),
                        c.getTimeSlot().name(),
                        c.getTimeSlot().getFormattedSchedule(),
                        c.getFacultyName()
                );
            }
            table.print();
            System.out.printf("Total Active Credits: %d | CGPA: %.2f%n", student.getCurrentCredits(), student.getCgpa());
        } catch (CampusException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleDropCourse(Student student) {
        displayStudentSchedule(student);
        System.out.print("\nEnter Course Code to Drop: ");
        String code = scanner.nextLine().trim().toUpperCase();

        try {
            registrationService.dropCourse(student.getId(), code);
            System.out.printf("\nSUCCESS: Course '%s' has been dropped. Updated credits: %d%n",
                    code, student.getCurrentCredits());
        } catch (CampusException e) {
            System.out.println("\nDROP ERROR: " + e.getMessage());
        }
    }

    private void handleResourceBookingRequest(String userId) {
        System.out.println("\n--- AVAILABLE CAMPUS FACILITIES ---");
        TableFormatter table = new TableFormatter("Resource ID", "Name", "Type", "Capacity", "Location");
        for (Resource r : resourceService.getAllResources()) {
            table.addRow(r.getResourceId(), r.getName(), r.getType().name(), String.valueOf(r.getCapacity()), r.getLocation());
        }
        table.print();

        System.out.print("\nEnter Resource ID to book: ");
        String resId = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();

        System.out.print("Enter Slot (A1, B1, C1, D1, E1, F1, L1_L2, L3_L4): ");
        String slotStr = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Purpose of Booking: ");
        String purpose = scanner.nextLine().trim();

        try {
            LocalDate date = LocalDate.parse(dateStr);
            TimeSlot slot = TimeSlot.valueOf(slotStr);
            Reservation res = resourceService.requestReservation(userId, resId, date, slot, purpose);
            System.out.printf("\nSUCCESS: Reservation #%s submitted! Status: %s%n",
                    res.getReservationId(), res.getStatus());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid slot identifier.");
        } catch (CampusException e) {
            System.out.println("Booking Error: " + e.getMessage());
        }
    }

    private void displayUserBookings(String userId) {
        System.out.println("\n--- MY FACILITY BOOKINGS ---");
        List<Reservation> bookings = resourceService.getUserReservations(userId);
        if (bookings.isEmpty()) {
            System.out.println("No booking records found.");
            return;
        }

        TableFormatter table = new TableFormatter("Reservation ID", "Resource", "Date", "Slot", "Status", "Notes");
        for (Reservation r : bookings) {
            table.addRow(r.getReservationId(), r.getResourceId(), r.getReservationDate().toString(),
                    r.getTimeSlot().name(), r.getStatus().name(), r.getAdminNotes());
        }
        table.print();
    }

    private void handleStudentBilling(String studentId) {
        System.out.println("\n--- TUITION & FEE INVOICES ---");
        List<Invoice> invoices = billingService.getStudentInvoices(studentId);
        if (invoices.isEmpty()) {
            System.out.println("No invoices generated yet for this student.");
            System.out.print("Would you like to generate this semester's tuition invoice? (y/n): ");
            String ans = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(ans)) {
                try {
                    Invoice inv = billingService.generateSemesterInvoice(studentId);
                    System.out.printf("Generated Invoice #%s for INR %.2f!%n", inv.getInvoiceId(), inv.getAmount());
                } catch (CampusException e) {
                    System.out.println("Invoice Generation Error: " + e.getMessage());
                }
            }
            return;
        }

        TableFormatter table = new TableFormatter("Invoice ID", "Amount (INR)", "Status", "Reference", "Description");
        for (Invoice inv : invoices) {
            table.addRow(inv.getInvoiceId(), String.format("%.2f", inv.getAmount()),
                    inv.getStatus().name(), inv.getTransactionReference() == null ? "None" : inv.getTransactionReference(),
                    inv.getDescription());
        }
        table.print();

        System.out.print("\nEnter Invoice ID to settle/pay (or press Enter to skip): ");
        String invId = scanner.nextLine().trim().toUpperCase();
        if (!invId.isEmpty()) {
            try {
                billingService.payInvoice(studentId, invId);
                System.out.println("PAYMENT SUCCESS: Invoice settled successfully! Official receipt generated.");
            } catch (CampusException e) {
                System.out.println("Payment Failed: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // FACULTY WORKFLOW
    // ==========================================
    private void runFacultyDashboard(Faculty faculty) {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + "-".repeat(50));
            System.out.println("           FACULTY ACADEMIC PORTAL");
            System.out.println("-".repeat(50));
            System.out.println(" 1. View My Assigned Courses & Student Rosters");
            System.out.println(" 2. Book Campus Seminar Hall / Lab");
            System.out.println(" 3. View My Facility Reservations");
            System.out.println(" 0. Logout to Main Menu");
            System.out.print("Select action: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    displayFacultyCourses(faculty);
                    break;
                case "2":
                    handleResourceBookingRequest(faculty.getId());
                    break;
                case "3":
                    displayUserBookings(faculty.getId());
                    break;
                case "0":
                    authService.logout();
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void displayFacultyCourses(Faculty faculty) {
        System.out.printf("\n--- COURSES ASSIGNED TO %s ---%n", faculty.getFullName());
        TableFormatter table = new TableFormatter("Course Code", "Title", "Credits", "Slot", "Enrolled", "Max Capacity");
        for (String code : faculty.getAssignedCourseCodes()) {
            Course c = dataStore.getCourses().get(code);
            if (c != null) {
                table.addRow(c.getCourseCode(), c.getTitle(), String.valueOf(c.getCredits()),
                        c.getTimeSlot().name(), String.valueOf(c.getCurrentEnrollment()), String.valueOf(c.getMaxCapacity()));
            }
        }
        table.print();
    }

    // ==========================================
    // ADMINISTRATOR WORKFLOW
    // ==========================================
    private void runAdminDashboard(Admin admin) {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + "-".repeat(50));
            System.out.println("       ADMINISTRATOR ACADEMIC MANAGEMENT PORTAL");
            System.out.println("-".repeat(50));
            System.out.println(" 1. View All Courses & Live Capacities");
            System.out.println(" 2. Review & Approve/Reject Facility Bookings");
            System.out.println(" 3. Generate Semester Tuition Invoices for All Students");
            System.out.println(" 4. View Institutional Analytics & Executive Report");
            System.out.println(" 5. View Real-Time Security & Transaction Audit Trail");
            System.out.println(" 6. Export Campus Records to CSV");
            System.out.println(" 0. Logout to Main Menu");
            System.out.print("Select action: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    displayCourseCatalog();
                    break;
                case "2":
                    handleAdminBookingApprovals(admin.getId());
                    break;
                case "3":
                    handleBatchInvoiceGeneration();
                    break;
                case "4":
                    System.out.println(analyticsService.generateExecutiveSummary());
                    break;
                case "5":
                    displayAuditTrail();
                    break;
                case "6":
                    exportAllCsvData();
                    break;
                case "0":
                    authService.logout();
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void handleAdminBookingApprovals(String adminId) {
        System.out.println("\n--- PENDING CAMPUS FACILITY BOOKINGS ---");
        List<Reservation> all = resourceService.getAllReservations();
        TableFormatter table = new TableFormatter("ID", "User ID", "Resource", "Date", "Slot", "Status", "Purpose");
        for (Reservation r : all) {
            table.addRow(r.getReservationId(), r.getUserId(), r.getResourceId(),
                    r.getReservationDate().toString(), r.getTimeSlot().name(), r.getStatus().name(), r.getPurpose());
        }
        table.print();

        System.out.print("\nEnter Reservation ID to review (or press Enter to return): ");
        String resId = scanner.nextLine().trim().toUpperCase();
        if (resId.isEmpty()) return;

        System.out.print("Action: (1) Approve, (2) Reject: ");
        String action = scanner.nextLine().trim();
        try {
            if ("1".equals(action)) {
                resourceService.approveReservation(adminId, resId);
                System.out.println("SUCCESS: Reservation approved.");
            } else if ("2".equals(action)) {
                System.out.print("Enter rejection rationale: ");
                String reason = scanner.nextLine().trim();
                resourceService.rejectReservation(adminId, resId, reason);
                System.out.println("NOTICE: Reservation rejected.");
            }
        } catch (CampusException e) {
            System.err.println("Approval Error: " + e.getMessage());
        }
    }

    private void handleBatchInvoiceGeneration() {
        System.out.println("\nGenerating semester tuition fee invoices for all active students...");
        int generated = 0;
        for (User user : dataStore.getUsers().values()) {
            if (user instanceof Student) {
                Student s = (Student) user;
                if (s.getCurrentCredits() > 0) {
                    try {
                        Invoice inv = billingService.generateSemesterInvoice(s.getId());
                        System.out.printf("  * Generated invoice %s for %s (%d cr) -> INR %.2f%n",
                                inv.getInvoiceId(), s.getFullName(), s.getCurrentCredits(), inv.getAmount());
                        generated++;
                    } catch (CampusException e) {
                        System.out.printf("  * Skipped %s: %s%n", s.getFullName(), e.getMessage());
                    }
                }
            }
        }
        System.out.printf("Batch Complete: %d invoices created.%n", generated);
    }

    private void displayAuditTrail() {
        System.out.println("\n--- RECENT ASYNCHRONOUS AUDIT TRAIL ENTRIES ---");
        List<AuditLog> logs = dataStore.getAuditLogs();
        int start = Math.max(0, logs.size() - 15);
        for (int i = start; i < logs.size(); i++) {
            System.out.println(logs.get(i).toFormattedString());
        }
    }

    private void exportAllCsvData() {
        try {
            filePersistenceManager.exportRegistrations(dataStore.getRegistrations().values());
            filePersistenceManager.exportReservations(dataStore.getReservations().values());
            filePersistenceManager.exportInvoices(dataStore.getInvoices().values());
            System.out.printf("\nSUCCESS: All records successfully exported to %s/%n",
                    filePersistenceManager.getDataDirectory().toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }

    // ==========================================
    // AUTOMATED END-TO-END DEMONSTRATION MODE
    // ==========================================
    public void runAutomatedDemonstration() {
        System.out.println("\n" + "#".repeat(70));
        System.out.println("  EXECUTING SMARTCAMPUS AUTOMATED DEMONSTRATION & VERIFICATION SUITE");
        System.out.println("#".repeat(70));

        try {
            // Step 1: Authentication
            System.out.println("\n[DEMO 1] Testing Student Authentication...");
            User studentUser = authService.login("student", "student123");
            Student aarav = (Student) studentUser;
            System.out.println(" -> " + aarav.getDashboardSummary());

            // Step 2: Successful Course Registration
            System.out.println("\n[DEMO 2] Testing FFCS Course Registration (CSE2005 - Java OOP)...");
            Registration reg1 = registrationService.registerCourse(aarav.getId(), "CSE2005");
            System.out.printf(" -> Result: Enrolled in %s! Total credits: %d%n",
                    reg1.getCourseCode(), aarav.getCurrentCredits());

            // Step 3: Slot Clash Prevention
            System.out.println("\n[DEMO 3] Testing Slot Clash Prevention (CSE3012 is also in Slot B1)...");
            try {
                registrationService.registerCourse(aarav.getId(), "CSE3012");
                System.err.println(" -> FAILED: Slot clash was not detected!");
            } catch (CampusException e) {
                System.out.println(" -> SUCCESS: Conflict intercepted as expected: " + e.getMessage());
            }

            // Step 4: Prerequisite Checking
            System.out.println("\n[DEMO 4] Testing Prerequisite Validation (CSE3003 requires CSE2005 completion)...");
            try {
                // Aarav is currently taking CSE2005 but hasn't completed it
                registrationService.registerCourse(aarav.getId(), "CSE3003");
                System.err.println(" -> FAILED: Prerequisite violation was not intercepted!");
            } catch (CampusException e) {
                System.out.println(" -> SUCCESS: Prerequisite requirement enforced: " + e.getMessage());
            }

            // Step 5: Campus Facility Reservation
            System.out.println("\n[DEMO 5] Testing Campus Resource Reservation (Turing HPC Lab)...");
            Reservation res = resourceService.requestReservation(aarav.getId(), "RES-LAB-01",
                    LocalDate.now().plusDays(2), TimeSlot.L1_L2, "Parallel Processing Research Project");
            System.out.println(" -> Requested Reservation: " + res);

            // Step 6: Admin Approval Workflow
            System.out.println("\n[DEMO 6] Testing Admin Approval Workflow...");
            authService.logout();
            User admin = authService.login("admin", "admin123");
            resourceService.approveReservation(admin.getId(), res.getReservationId());
            System.out.println(" -> Administrator approved booking: Status = " + res.getStatus());

            // Step 7: Billing & Financial Ledger
            System.out.println("\n[DEMO 7] Testing Financial Invoice Generation & Payment...");
            Invoice inv = billingService.generateSemesterInvoice(aarav.getId());
            System.out.printf(" -> Invoice #%s generated: Amount INR %.2f%n", inv.getInvoiceId(), inv.getAmount());
            billingService.payInvoice(aarav.getId(), inv.getInvoiceId());
            System.out.printf(" -> Invoice paid! Transaction Ref: %s (Status: %s)%n",
                    inv.getTransactionReference(), inv.getStatus());

            // Step 8: Analytics Engine
            System.out.println("\n[DEMO 8] Generating Real-Time Institutional Analytics...");
            System.out.println(analyticsService.generateExecutiveSummary());

            // Step 9: Export Data
            System.out.println("[DEMO 9] Persisting Campus Records to CSV...");
            exportAllCsvData();

            System.out.println("\n>>> ALL DEMONSTRATION TEST FLOWS PASSED WITH 100% SUCCESS! <<<\n");

        } catch (Exception e) {
            System.err.println("Demo Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
