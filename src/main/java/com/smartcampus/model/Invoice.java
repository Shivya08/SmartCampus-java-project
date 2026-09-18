package com.smartcampus.model;

import java.time.LocalDateTime;

/**
 * Invoice entity representing financial obligations, credit fee calculations,
 * and payment transactions.
 */
public class Invoice {
    public enum PaymentStatus {
        UNPAID,
        PAID,
        CANCELLED
    }

    private final String invoiceId;
    private final String studentId;
    private final double amount;
    private final String description;
    private final LocalDateTime generatedAt;
    private LocalDateTime paidAt;
    private PaymentStatus status;
    private String transactionReference;

    public Invoice(String invoiceId, String studentId, double amount, String description) {
        this.invoiceId = invoiceId;
        this.studentId = studentId;
        this.amount = amount;
        this.description = description;
        this.generatedAt = LocalDateTime.now();
        this.status = PaymentStatus.UNPAID;
        this.transactionReference = null;
    }

    public void markPaid(String transactionReference) {
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.transactionReference = transactionReference;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    @Override
    public String toString() {
        return String.format("Invoice #%s: Student %s | INR %.2f | Status: %s | Desc: %s",
                invoiceId, studentId, amount, status, description);
    }
}
