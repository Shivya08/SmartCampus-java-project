package com.smartcampus.service;

import com.smartcampus.model.*;
import com.smartcampus.repository.InMemoryDataStore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Academic Analytics and Institutional Intelligence Service.
 * Leverages modern Java Stream API, Lambdas, Grouping Collectors,
 * and statistical aggregators to produce operational reports.
 */
public class AnalyticsService {
    private final InMemoryDataStore dataStore;

    public AnalyticsService() {
        this.dataStore = InMemoryDataStore.getInstance();
    }

    /**
     * Calculates the distribution of students across academic departments using groupingBy collector.
     */
    public Map<String, Long> getDepartmentalStudentDistribution() {
        return dataStore.getUsers().values().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));
    }

    /**
     * Identifies the most popular courses by active enrollment count.
     */
    public List<Course> getTopCoursesByEnrollment(int limit) {
        return dataStore.getCourses().values().stream()
                .sorted(Comparator.comparingInt(Course::getCurrentEnrollment).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Analyzes faculty teaching load (number of assigned courses per faculty member).
     */
    public Map<String, Integer> getFacultyTeachingLoads() {
        return dataStore.getUsers().values().stream()
                .filter(u -> u instanceof Faculty)
                .map(u -> (Faculty) u)
                .collect(Collectors.toMap(
                        Faculty::getFullName,
                        f -> f.getAssignedCourseCodes().size(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    /**
     * Computes the university-wide average CGPA across all registered students.
     */
    public double getAverageStudentCgpa() {
        return dataStore.getUsers().values().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .mapToDouble(Student::getCgpa)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculates total tuition revenue collected across settled invoices.
     */
    public double getTotalSettledRevenue() {
        return dataStore.getInvoices().values().stream()
                .filter(inv -> inv.getStatus() == Invoice.PaymentStatus.PAID)
                .mapToDouble(Invoice::getAmount)
                .sum();
    }

    /**
     * Calculates pending accounts receivable.
     */
    public double getTotalOutstandingReceivables() {
        return dataStore.getInvoices().values().stream()
                .filter(inv -> inv.getStatus() == Invoice.PaymentStatus.UNPAID)
                .mapToDouble(Invoice::getAmount)
                .sum();
    }

    /**
     * Generates a comprehensive Institutional Executive Summary report.
     */
    public String generateExecutiveSummary() {
        long totalStudents = dataStore.getUsers().values().stream().filter(u -> u instanceof Student).count();
        long totalFaculty = dataStore.getUsers().values().stream().filter(u -> u instanceof Faculty).count();
        long totalCourses = dataStore.getCourses().size();
        long totalRegistrations = dataStore.getRegistrations().values().stream()
                .filter(r -> r.getStatus() == Registration.Status.REGISTERED).count();
        long totalReservations = dataStore.getReservations().size();

        StringBuilder sb = new StringBuilder();
        sb.append("===============================================================\n");
        sb.append("         SMARTCAMPUS EXECUTIVE ANALYTICS REPORT                \n");
        sb.append("===============================================================\n");
        sb.append(String.format(" Total Enrolled Students  : %d\n", totalStudents));
        sb.append(String.format(" Total Teaching Faculty   : %d\n", totalFaculty));
        sb.append(String.format(" Active Course Offerings  : %d\n", totalCourses));
        sb.append(String.format(" Confirmed Enrollments    : %d\n", totalRegistrations));
        sb.append(String.format(" Average Student CGPA     : %.2f / 10.00\n", getAverageStudentCgpa()));
        sb.append(String.format(" Campus Facility Bookings : %d\n", totalReservations));
        sb.append(String.format(" Settled Tuition Revenue  : INR %,.2f\n", getTotalSettledRevenue()));
        sb.append(String.format(" Outstanding Receivables  : INR %,.2f\n", getTotalOutstandingReceivables()));
        sb.append("---------------------------------------------------------------\n");
        sb.append(" Top Enrolled Courses:\n");
        for (Course c : getTopCoursesByEnrollment(3)) {
            sb.append(String.format("   * [%s] %-35s : %d/%d seats\n",
                    c.getCourseCode(), c.getTitle(), c.getCurrentEnrollment(), c.getMaxCapacity()));
        }
        sb.append("===============================================================\n");
        return sb.toString();
    }
}
