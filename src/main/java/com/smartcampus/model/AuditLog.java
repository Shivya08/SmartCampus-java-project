package com.smartcampus.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AuditLog entity capturing security events, transaction logs, and operations
 * processed asynchronously by the background audit subsystem.
 */
public class AuditLog {
    public enum Severity {
        INFO,
        WARN,
        ERROR,
        SECURITY
    }

    private final String logId;
    private final LocalDateTime timestamp;
    private final String actorId;
    private final String action;
    private final String details;
    private final Severity severity;

    public AuditLog(String logId, String actorId, String action, String details, Severity severity) {
        this.logId = logId;
        this.timestamp = LocalDateTime.now();
        this.actorId = actorId;
        this.action = action;
        this.details = details;
        this.severity = severity;
    }

    public String getLogId() {
        return logId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getActorId() {
        return actorId;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String toFormattedString() {
        return String.format("[%s] [%-8s] [Actor: %-10s] Action: %-25s | %s",
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                severity, actorId, action, details);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }
}
