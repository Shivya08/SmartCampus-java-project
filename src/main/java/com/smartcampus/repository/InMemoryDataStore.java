package com.smartcampus.repository;

import com.smartcampus.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe In-Memory Data Store serving as the primary repository cache.
 * Implements the Singleton pattern and uses Concurrent Collections for thread safety.
 */
public class InMemoryDataStore {
    private static volatile InMemoryDataStore instance;

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();
    private final Map<String, Resource> resources = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final Map<String, Invoice> invoices = new ConcurrentHashMap<>();
    private final List<AuditLog> auditLogs = new CopyOnWriteArrayList<>();

    private final AtomicLong regIdGen = new AtomicLong(1000);
    private final AtomicLong resIdGen = new AtomicLong(5000);
    private final AtomicLong invIdGen = new AtomicLong(9000);
    private final AtomicLong logIdGen = new AtomicLong(1);

    private InMemoryDataStore() {
        seedInitialData();
    }

    public static InMemoryDataStore getInstance() {
        if (instance == null) {
            synchronized (InMemoryDataStore.class) {
                if (instance == null) {
                    instance = new InMemoryDataStore();
                }
            }
        }
        return instance;
    }

    private void seedInitialData() {
        // 1. Seed Administrators
        Admin admin = new Admin("ADM001", "admin", "admin123",
                "Dr. S. K. Narayanan", "admin@vit.ac.in", "DEAN_ACADEMICS", "Academic Block 1 - 402");
        users.put(admin.getUsername(), admin);

        // 2. Seed Faculty Members
        Faculty fac1 = new Faculty("FAC101", "prof.ananya", "faculty123",
                "Dr. Ananya Sharma", "ananya.sharma@vit.ac.in", "EMP0891", "Computer Science & Engineering", "Professor");
        Faculty fac2 = new Faculty("FAC102", "prof.rajesh", "faculty123",
                "Dr. Rajesh Verma", "rajesh.verma@vit.ac.in", "EMP0742", "Computer Science & Engineering", "Associate Professor");
        Faculty fac3 = new Faculty("FAC103", "prof.meenakshi", "faculty123",
                "Dr. Meenakshi Sundaram", "meenakshi.s@vit.ac.in", "EMP0623", "Mathematics & Data Science", "Professor");

        users.put(fac1.getUsername(), fac1);
        users.put(fac2.getUsername(), fac2);
        users.put(fac3.getUsername(), fac3);

        // 3. Seed Students
        Student s1 = new Student("STU201", "student", "student123",
                "Aarav Patel", "aarav.patel2024@vitstudent.ac.in",
                "24BCY10203", "Computer Science & Engineering (Cyber Security)", 3, 9.15);
        s1.addCompletedCourse("CSE1001"); // Completed Problem Solving & Python
        s1.addCompletedCourse("MAT1001"); // Completed Calculus

        Student s2 = new Student("STU202", "priya.nair", "student123",
                "Priya Nair", "priya.nair2024@vitstudent.ac.in",
                "24BCS10145", "Computer Science & Engineering", 3, 8.84);
        s2.addCompletedCourse("CSE1001");

        users.put(s1.getUsername(), s1);
        users.put(s2.getUsername(), s2);

        // 4. Seed Courses (FFCS style with Slots and Prerequisites)
        // CSE1001: Intro to Programming (Prerequisite for higher CS courses)
        Course c1 = new Course("CSE1001", "Problem Solving & Programming", 4,
                "CSE", fac1.getId(), fac1.getFullName(), 60, TimeSlot.A1, null);

        // CSE2005: Object-Oriented Programming (Requires CSE1001)
        Course c2 = new Course("CSE2005", "Object-Oriented Programming in Java", 4,
                "CSE", fac1.getId(), fac1.getFullName(), 50, TimeSlot.B1, "CSE1001");

        // CSE3003: Operating Systems (Requires CSE2005)
        Course c3 = new Course("CSE3003", "Operating System Concepts", 4,
                "CSE", fac2.getId(), fac2.getFullName(), 45, TimeSlot.C1, "CSE2005");

        // MAT2005: Transform Techniques & Difference Equations (Requires MAT1001)
        Course c4 = new Course("MAT2005", "Transform Techniques & Difference Equations", 4,
                "MAT", fac3.getId(), fac3.getFullName(), 60, TimeSlot.D1, "MAT1001");

        // CSE3012: Advanced Network & Cyber Security (Slot B1 - deliberate clash with CSE2005)
        Course c5 = new Course("CSE3012", "Network & Cyber Security Engineering", 3,
                "CSE", fac2.getId(), fac2.getFullName(), 40, TimeSlot.B1, "CSE1001");

        // CSE2005L: OOP Lab (Lab Slot L1_L2)
        Course c6 = new Course("CSE2005L", "Object-Oriented Programming Laboratory", 1,
                "CSE", fac1.getId(), fac1.getFullName(), 35, TimeSlot.L1_L2, "CSE1001");

        courses.put(c1.getCourseCode(), c1);
        courses.put(c2.getCourseCode(), c2);
        courses.put(c3.getCourseCode(), c3);
        courses.put(c4.getCourseCode(), c4);
        courses.put(c5.getCourseCode(), c5);
        courses.put(c6.getCourseCode(), c6);

        fac1.assignCourse(c1.getCourseCode());
        fac1.assignCourse(c2.getCourseCode());
        fac1.assignCourse(c6.getCourseCode());
        fac2.assignCourse(c3.getCourseCode());
        fac2.assignCourse(c5.getCourseCode());
        fac3.assignCourse(c4.getCourseCode());

        // 5. Seed Campus Resources
        Resource r1 = new Resource("RES-LAB-01", "Turing High-Performance Computing Lab",
                Resource.ResourceType.LABORATORY, 75, "Technology Tower - Level 3");
        Resource r2 = new Resource("RES-AUD-01", "Dr. A.P.J. Abdul Kalam Auditorium",
                Resource.ResourceType.SEMINAR_HALL, 350, "Central Convention Centre");
        Resource r3 = new Resource("RES-POD-01", "Innovation Collaborative Study Pod C-4",
                Resource.ResourceType.STUDY_POD, 8, "Central Library - 2nd Floor");
        Resource r4 = new Resource("RES-CLS-01", "Smart Interactive Multimedia Classroom 501",
                Resource.ResourceType.SMART_CLASSROOM, 60, "Academic Block 2 - Level 5");

        resources.put(r1.getResourceId(), r1);
        resources.put(r2.getResourceId(), r2);
        resources.put(r3.getResourceId(), r3);
        resources.put(r4.getResourceId(), r4);
    }

    // Accessor and Helper Methods
    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

    public Map<String, Registration> getRegistrations() {
        return registrations;
    }

    public Map<String, Resource> getResources() {
        return resources;
    }

    public Map<String, Reservation> getReservations() {
        return reservations;
    }

    public Map<String, Invoice> getInvoices() {
        return invoices;
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public String nextRegistrationId() {
        return "REG-" + regIdGen.incrementAndGet();
    }

    public String nextReservationId() {
        return "RES-" + resIdGen.incrementAndGet();
    }

    public String nextInvoiceId() {
        return "INV-" + invIdGen.incrementAndGet();
    }

    public String nextLogId() {
        return "LOG-" + logIdGen.incrementAndGet();
    }
}
