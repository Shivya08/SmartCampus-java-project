package com.smartcampus;

import com.smartcampus.cli.ConsoleUI;
import com.smartcampus.concurrency.AsyncAuditLogger;
import com.smartcampus.concurrency.SystemMonitorWorker;

/**
 * Main application entry point for the SmartCampus University Management System.
 * Supports interactive console UI mode and headless automated demonstration mode.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize background daemon worker
        SystemMonitorWorker monitor = new SystemMonitorWorker();
        monitor.startMonitoring();

        // Register JVM graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[SYSTEM] Initiating clean shutdown. Flushing audit queue...");
            AsyncAuditLogger.getInstance().shutdown();
            monitor.stop();
            System.out.println("[SYSTEM] Shutdown completed successfully.");
        }, "SmartCampus-ShutdownHook"));

        ConsoleUI ui = new ConsoleUI();

        if (args.length > 0 && ("--demo".equalsIgnoreCase(args[0]) || "--test".equalsIgnoreCase(args[0]))) {
            System.out.println("Starting in automated test & demonstration mode...");
            ui.runAutomatedDemonstration();
            System.exit(0);
        } else {
            ui.start();
        }
    }
}
