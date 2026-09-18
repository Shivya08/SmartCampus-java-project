package com.smartcampus.repository;

import com.smartcampus.model.AuditLog;
import com.smartcampus.model.Invoice;
import com.smartcampus.model.Registration;
import com.smartcampus.model.Reservation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

/**
 * FilePersistenceManager handles persistent storage and export of campus records to disk.
 * Demonstrates Java NIO.2, Try-With-Resources, and robust File Stream handling.
 */
public class FilePersistenceManager {
    private final Path dataDirectory;

    public FilePersistenceManager(String dataDirPath) {
        this.dataDirectory = Paths.get(dataDirPath);
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    public synchronized void exportRegistrations(Collection<Registration> registrations) throws IOException {
        Path filePath = dataDirectory.resolve("registrations.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("RegistrationId,StudentId,CourseCode,Status,Grade,Timestamp");
            writer.newLine();
            for (Registration reg : registrations) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s",
                        reg.getRegistrationId(),
                        reg.getStudentId(),
                        reg.getCourseCode(),
                        reg.getStatus(),
                        reg.getGrade(),
                        reg.getRegisteredAt()));
                writer.newLine();
            }
        }
    }

    public synchronized void exportReservations(Collection<Reservation> reservations) throws IOException {
        Path filePath = dataDirectory.resolve("reservations.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("ReservationId,UserId,ResourceId,Date,TimeSlot,Status,Purpose");
            writer.newLine();
            for (Reservation res : reservations) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,\"%s\"",
                        res.getReservationId(),
                        res.getUserId(),
                        res.getResourceId(),
                        res.getReservationDate(),
                        res.getTimeSlot(),
                        res.getStatus(),
                        res.getPurpose().replace("\"", "\"\"")));
                writer.newLine();
            }
        }
    }

    public synchronized void exportInvoices(Collection<Invoice> invoices) throws IOException {
        Path filePath = dataDirectory.resolve("invoices.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("InvoiceId,StudentId,Amount,Status,Reference,GeneratedAt");
            writer.newLine();
            for (Invoice inv : invoices) {
                writer.write(String.format("%s,%s,%.2f,%s,%s,%s",
                        inv.getInvoiceId(),
                        inv.getStudentId(),
                        inv.getAmount(),
                        inv.getStatus(),
                        inv.getTransactionReference() == null ? "N/A" : inv.getTransactionReference(),
                        inv.getGeneratedAt()));
                writer.newLine();
            }
        }
    }

    public synchronized void appendAuditLog(AuditLog log) {
        Path filePath = dataDirectory.resolve("audit_trail.log");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(log.toFormattedString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to append audit log: " + e.getMessage());
        }
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
