package com.smartcampus.concurrency;

import com.smartcampus.model.Course;
import com.smartcampus.repository.InMemoryDataStore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background daemon service running scheduled operational monitors.
 * Demonstrates ScheduledExecutorService and background task management.
 */
public class SystemMonitorWorker {
    private final ScheduledExecutorService scheduler;
    private final InMemoryDataStore dataStore;
    private final AsyncAuditLogger logger;

    public SystemMonitorWorker() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SystemMonitor-Thread");
            t.setDaemon(true);
            return t;
        });
        this.dataStore = InMemoryDataStore.getInstance();
        this.logger = AsyncAuditLogger.getInstance();
    }

    public void startMonitoring() {
        // Run health check every 30 seconds
        scheduler.scheduleAtFixedRate(this::performSystemHealthCheck, 5, 30, TimeUnit.SECONDS);
    }

    private void performSystemHealthCheck() {
        try {
            int highEnrollmentCount = 0;
            for (Course course : dataStore.getCourses().values()) {
                double fillRatio = (double) course.getCurrentEnrollment() / course.getMaxCapacity();
                if (fillRatio >= 0.90) {
                    highEnrollmentCount++;
                }
            }
            if (highEnrollmentCount > 0) {
                logger.logInfo("SYSTEM_MONITOR", "CAPACITY_ALERT",
                        String.format("%d courses have reached >=90%% seat capacity.", highEnrollmentCount));
            }
        } catch (Exception e) {
            logger.logError("SYSTEM_MONITOR", "HEALTH_CHECK_FAILURE", e.getMessage());
        }
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
