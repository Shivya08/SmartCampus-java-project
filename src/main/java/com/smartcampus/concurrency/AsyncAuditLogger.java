package com.smartcampus.concurrency;

import com.smartcampus.model.AuditLog;
import com.smartcampus.repository.FilePersistenceManager;
import com.smartcampus.repository.InMemoryDataStore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * High-performance Asynchronous Audit Logger.
 * Uses a Producer-Consumer pattern with a thread-safe BlockingQueue and a dedicated daemon worker thread.
 * Ensures system transaction processing is never stalled by disk I/O.
 */
public class AsyncAuditLogger {
    private static volatile AsyncAuditLogger instance;

    private final BlockingQueue<AuditLog> logQueue;
    private final Thread workerThread;
    private final AtomicBoolean running;
    private final InMemoryDataStore dataStore;
    private final FilePersistenceManager persistenceManager;

    private AsyncAuditLogger() {
        this.logQueue = new LinkedBlockingQueue<>(5000);
        this.running = new AtomicBoolean(true);
        this.dataStore = InMemoryDataStore.getInstance();
        this.persistenceManager = new FilePersistenceManager("data");

        this.workerThread = new Thread(this::processLogs, "AsyncAuditLogger-Worker");
        this.workerThread.setDaemon(true);
        this.workerThread.start();
    }

    public static AsyncAuditLogger getInstance() {
        if (instance == null) {
            synchronized (AsyncAuditLogger.class) {
                if (instance == null) {
                    instance = new AsyncAuditLogger();
                }
            }
        }
        return instance;
    }

    /**
     * Non-blocking log submission.
     */
    public void log(String actorId, String action, String details, AuditLog.Severity severity) {
        String logId = dataStore.nextLogId();
        AuditLog entry = new AuditLog(logId, actorId, action, details, severity);
        boolean offered = logQueue.offer(entry);
        if (!offered) {
            System.err.println("WARN: Audit log queue full, dropped entry: " + entry);
        }
    }

    public void logInfo(String actorId, String action, String details) {
        log(actorId, action, details, AuditLog.Severity.INFO);
    }

    public void logWarning(String actorId, String action, String details) {
        log(actorId, action, details, AuditLog.Severity.WARN);
    }

    public void logSecurity(String actorId, String action, String details) {
        log(actorId, action, details, AuditLog.Severity.SECURITY);
    }

    public void logError(String actorId, String action, String details) {
        log(actorId, action, details, AuditLog.Severity.ERROR);
    }

    private void processLogs() {
        while (running.get() || !logQueue.isEmpty()) {
            try {
                AuditLog logEntry = logQueue.poll(500, TimeUnit.MILLISECONDS);
                if (logEntry != null) {
                    dataStore.getAuditLogs().add(logEntry);
                    persistenceManager.appendAuditLog(logEntry);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        running.set(false);
        try {
            workerThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
