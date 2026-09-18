package com.smartcampus;

import com.smartcampus.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for the Institutional Analytics Engine.
 */
public class AnalyticsServiceTest {
    private AnalyticsService analyticsService;

    @BeforeEach
    public void setUp() {
        this.analyticsService = new AnalyticsService();
    }

    @Test
    @DisplayName("Should correctly calculate departmental distribution of students")
    public void testDepartmentalDistribution() {
        Map<String, Long> distribution = analyticsService.getDepartmentalStudentDistribution();
        assertNotNull(distribution);
        assertFalse(distribution.isEmpty());
    }

    @Test
    @DisplayName("Should calculate a valid non-zero average student CGPA")
    public void testAverageCgpaCalculation() {
        double avg = analyticsService.getAverageStudentCgpa();
        assertTrue(avg > 0.0 && avg <= 10.0, "Average CGPA must fall within 0.0 to 10.0 range");
    }

    @Test
    @DisplayName("Should generate a non-empty executive summary report")
    public void testExecutiveSummary() {
        String summary = analyticsService.generateExecutiveSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("SMARTCAMPUS EXECUTIVE ANALYTICS REPORT"));
    }
}
