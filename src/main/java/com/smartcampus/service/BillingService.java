package com.smartcampus.service;

import com.smartcampus.concurrency.AsyncAuditLogger;
import com.smartcampus.exception.CampusException;
import com.smartcampus.exception.EntityNotFoundException;
import com.smartcampus.model.Course;
import com.smartcampus.model.Invoice;
import com.smartcampus.model.Student;
import com.smartcampus.model.User;
import com.smartcampus.repository.InMemoryDataStore;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Billing, Academic Tuition, and Financial Ledger Service.
 * Manages per-credit fee computations, invoice creation, and transaction reconciliation.
 */
public class BillingService {
    public static final double TUITION_FEE_PER_CREDIT = 4500.00;
    public static final double CAMPUS_DEVELOPMENT_FEE = 12000.00;
    public static final double LAB_FEE_PER_COURSE = 2500.00;

    private final InMemoryDataStore dataStore;
    private final AsyncAuditLogger logger;

    public BillingService() {
        this.dataStore = InMemoryDataStore.getInstance();
        this.logger = AsyncAuditLogger.getInstance();
    }

    /**
     * Generates a detailed semester tuition fee invoice based on the student's registered credits.
     */
    public synchronized Invoice generateSemesterInvoice(String studentId) throws CampusException {
        Student student = findStudentById(studentId);

        if (student.getCurrentCredits() == 0) {
            throw new CampusException("NO_CREDITS_REGISTERED",
                    "Cannot generate invoice: Student has not registered for any credits yet.");
        }

        double tuitionTotal = student.getCurrentCredits() * TUITION_FEE_PER_CREDIT;
        long labCourses = student.getRegisteredCourseCodes().stream()
                .map(code -> dataStore.getCourses().get(code))
                .filter(c -> c != null && c.getTitle().toLowerCase().contains("lab"))
                .count();

        double labTotal = labCourses * LAB_FEE_PER_COURSE;
        double totalAmount = tuitionTotal + labTotal + CAMPUS_DEVELOPMENT_FEE;

        String invId = dataStore.nextInvoiceId();
        String description = String.format("Semester Tuition Fee (%d Credits @ %.0f/cr) + %d Lab(s) + Campus Dev Fee",
                student.getCurrentCredits(), TUITION_FEE_PER_CREDIT, labCourses);

        Invoice invoice = new Invoice(invId, student.getId(), totalAmount, description);
        dataStore.getInvoices().put(invId, invoice);

        logger.logInfo(studentId, "INVOICE_GENERATED",
                String.format("Generated invoice %s for INR %.2f", invId, totalAmount));

        return invoice;
    }

    /**
     * Processes payment for an outstanding invoice.
     */
    public synchronized boolean payInvoice(String studentId, String invoiceId) throws CampusException {
        Invoice invoice = dataStore.getInvoices().get(invoiceId);
        if (invoice == null) {
            throw new EntityNotFoundException("Invoice", invoiceId);
        }

        if (!invoice.getStudentId().equalsIgnoreCase(studentId)) {
            throw new CampusException("UNAUTHORIZED_PAYMENT", "Cannot pay invoice belonging to another student.");
        }

        if (invoice.getStatus() == Invoice.PaymentStatus.PAID) {
            throw new CampusException("ALREADY_PAID", "This invoice has already been fully settled.");
        }

        String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        invoice.markPaid(txnRef);

        logger.logInfo(studentId, "PAYMENT_SETTLED",
                String.format("Settled invoice %s of INR %.2f (Ref: %s)", invoiceId, invoice.getAmount(), txnRef));

        return true;
    }

    public List<Invoice> getStudentInvoices(String studentId) {
        return dataStore.getInvoices().values().stream()
                .filter(inv -> inv.getStudentId().equalsIgnoreCase(studentId))
                .collect(Collectors.toList());
    }

    public List<Invoice> getAllInvoices() {
        return List.copyOf(dataStore.getInvoices().values());
    }

    private Student findStudentById(String studentId) throws EntityNotFoundException {
        for (User user : dataStore.getUsers().values()) {
            if (user instanceof Student && (user.getId().equalsIgnoreCase(studentId) || user.getUsername().equalsIgnoreCase(studentId))) {
                return (Student) user;
            }
        }
        throw new EntityNotFoundException("Student", studentId);
    }
}
