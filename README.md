# SmartCampus: University Academic & Resource Management System

[![Java Version](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Build Tool](https://img.shields.io/badge/Build-Maven%20%7C%20Batch-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Academic%20Evaluation-green.svg)]()
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform%20%28Windows%2FLinux%2FmacOS%29-brightgreen.svg)]()

> Developed as an original flipped course engineering project for **VITyarthi - Build Your Own Project** evaluation.

---

## 1. Project Title & Overview

**SmartCampus** is an enterprise-grade, modular Object-Oriented Java application designed to automate and streamline core university academic operations. Modeled closely around the **Fully Flexible Credit System (FFCS)** popularized by premier academic institutions such as Vellore Institute of Technology (VIT), the system provides an integrated platform for:
1. **Intelligent Course Registration & Slot Clash Interception**: Prevents schedule conflicts between theory/lab slots and enforces course prerequisites.
2. **Dynamic Seat Allocation & Automated Waitlist Promotion**: Uses thread-safe queues to automatically promote waitlisted students when seats become available.
3. **Campus Infrastructure Reservation**: Coordinates physical resource bookings (high-performance computing labs, seminar auditoriums, study pods) with conflict prevention and administrative approval workflows.
4. **Academic Tuition & Financial Ledger**: Automates per-credit fee computations, invoice issuance, and payment receipt reconciliation.
5. **High-Performance Asynchronous Auditing**: Employs a multithreaded producer-consumer logging subsystem using `BlockingQueue` and background worker threads.
6. **Stream-Driven Institutional Analytics**: Leverages modern Java Stream API and functional reductions for real-time executive reporting.

---

## 2. Key Features

- **Role-Based Access Control (RBAC)**: Distinct dashboards and privilege levels for `Student`, `Faculty`, and `Admin` users.
- **Cryptographic Security**: Secure SHA-256 password hashing for identity validation.
- **FFCS Timetable Slot Clash Detection**: Algorithmic validation preventing students from registering for courses with overlapping day/time windows.
- **Prerequisite Validation**: Guarding higher-level coursework against missing foundation subjects.
- **Automated Waitlist Promotion Engine**: First-In-First-Out promotion logic when an enrolled student withdraws or drops a course.
- **Campus Facility Scheduling**: Real-time room capacity tracking and conflict detection for laboratories, auditoriums, and project pods.
- **Tuition Invoice Generation & Settlement**: Automatic fee computation based on registered credits and lab components.
- **Asynchronous Audit Logging**: Non-blocking concurrent audit logger preventing disk I/O bottlenecks.
- **Data Persistence & CSV Exporter**: Java NIO.2 file storage for data preservation and external reporting.
- **Interactive Console UI**: Beautiful ASCII table-formatted terminal interface.

---

## 3. Technologies and Tools Used

| Category | Technology / Tool | Purpose |
| :--- | :--- | :--- |
| **Language** | Java 17+ (LTS) | Core object-oriented programming implementation |
| **Architecture** | Layered Modular Architecture | Separation of Model, Service, Repository, Concurrency, and CLI |
| **Concurrency** | `java.util.concurrent` | `BlockingQueue`, `ExecutorService`, `ConcurrentHashMap`, `AtomicLong` |
| **Functional Programming** | Java Stream API & Lambdas | Statistical aggregations, grouping collectors, and analytics |
| **I/O & Persistence** | Java NIO.2 (`Files`, `Paths`) | File stream writing, CSV export, and log persistence |
| **Build Automation** | Apache Maven & Windows Batch | Clean compilation, packaging, and execution |
| **Testing** | JUnit 5 & Standalone Test Runner | Unit testing of core business logic and conflict detection |
| **Documentation** | Markdown, Mermaid, Chrome PDF | High-definition PDF project report and UML diagrams |

---

## 4. System Architecture & Package Structure

```
com.smartcampus
│
├── Main.java                          # Application Entry Point & Lifecycle Hooks
│
├── model/                             # Domain Entities (OOP Abstraction & Inheritance)
│   ├── User.java                      # Abstract Base User with SHA-256 Hashing
│   ├── Role.java                      # RBAC Enumeration (STUDENT, FACULTY, ADMIN)
│   ├── Student.java                   # Student Entity (Roll No, CGPA, Credits)
│   ├── Faculty.java                   # Faculty Entity (Designation, Workload)
│   ├── Admin.java                     # Administrator Entity (Permissions)
│   ├── Course.java                    # Course Catalog Model & Waitlist Queue
│   ├── TimeSlot.java                  # Slot Enum with Clash Detection Logic
│   ├── Registration.java              # Enrollment Record & Lifecycle Status
│   ├── Resource.java                  # Physical Campus Infrastructure Model
│   ├── Reservation.java               # Facility Booking Request Model
│   ├── Invoice.java                   # Financial Ledger & Payment Record
│   └── AuditLog.java                  # Security & Transaction Audit Entry
│
├── exception/                         # Custom Exception Hierarchy
│   ├── CampusException.java           # Base Checked Exception
│   ├── AuthenticationException.java   # Credential & Access Failures
│   ├── PrerequisiteNotMetException.java# Missing Prerequisite Guard
│   ├── SlotClashException.java        # Timetable Conflict Interceptor
│   ├── CreditLimitExceededException.java# Max Credit Cap Guard
│   ├── ResourceUnavailableException.java# Booking Collision Interceptor
│   └── EntityNotFoundException.java   # Missing Entity Lookup Error
│
├── repository/                        # Data Access Layer
│   ├── DataRepository.java            # Generic Repository Interface (Generics)
│   ├── InMemoryDataStore.java         # Thread-Safe Singleton Data Cache
│   └── FilePersistenceManager.java    # Java NIO.2 Disk Persistence & CSV Exporter
│
├── service/                           # Business Logic Services
│   ├── AuthService.java               # Authentication & Session Management
│   ├── RegistrationService.java       # FFCS Engine & Waitlist Promotion
│   ├── ResourceService.java           # Facility Booking & Admin Approvals
│   ├── BillingService.java            # Per-Credit Tuition & Invoicing
│   └── AnalyticsService.java          # Java Streams Institutional Intelligence
│
├── concurrency/                       # Multithreading Subsystem
│   ├── AsyncAuditLogger.java          # Producer-Consumer Async Logger
│   └── SystemMonitorWorker.java       # Scheduled Background Monitor
│
├── util/                              # Reusable Utility Helpers
│   ├── TableFormatter.java            # ASCII Table Console Renderer
│   └── Validator.java                 # Regex & Format Sanitizers
│
└── cli/                               # User Presentation Layer
    └── ConsoleUI.java                 # Interactive Terminal Menus for All Roles
```

---

## 5. Steps to Install & Run the Project

### Prerequisites
- Java Development Kit (JDK 17 or higher recommended, JDK 11+ compatible).
- (Optional) Apache Maven 3.8+ if building via Maven.

### Running with Windows Batch Scripts (Easiest)
1. Open Command Prompt or PowerShell in the project root:
   ```cmd
   cd C:\Users\hp\.gemini\antigravity\scratch\SmartCampus
   ```
2. Build the project:
   ```cmd
   build.bat
   ```
3. Run the interactive console application:
   ```cmd
   run.bat
   ```
4. Run the automated end-to-end demonstration:
   ```cmd
   run.bat --demo
   ```

### Running with Apache Maven
1. Compile and package the project:
   ```bash
   mvn clean compile package
   ```
2. Run the generated executable JAR:
   ```bash
   java -jar target/smartcampus-core-1.0.0.jar
   ```

---

## 6. Pre-Configured Test Credentials

| Role | Username | Password | Notes |
| :--- | :--- | :--- | :--- |
| **Student** | `student` | `student123` | Aarav Patel (Roll: 24BCY10203, CGPA: 9.15) |
| **Student** | `priya.nair` | `student123` | Priya Nair (Roll: 24BCS10145, CGPA: 8.84) |
| **Faculty** | `prof.ananya` | `faculty123` | Dr. Ananya Sharma (CSE Professor) |
| **Faculty** | `prof.rajesh` | `faculty123` | Dr. Rajesh Verma (CSE Associate Prof) |
| **Admin** | `admin` | `admin123` | Dr. S. K. Narayanan (Dean Academics) |

---

## 7. Instructions for Testing

### Option A: Standalone Zero-Dependency Test Suite
SmartCampus includes a standalone test runner that requires no external Maven dependencies:
```cmd
run.bat --test
```
or directly:
```bash
javac -cp target/classes -d target/test-classes src/test/java/com/smartcampus/StandaloneTestRunner.java
java -cp target/classes;target/test-classes com.smartcampus.StandaloneTestRunner
```

### Option B: Maven Surefire Test Execution
```bash
mvn test
```

### What is Tested:
- **FFCS Registration Success**: Validates course enrollment under valid conditions.
- **Slot Clash Detection**: Tests that registering conflicting courses throws `SlotClashException`.
- **Prerequisite Enforcement**: Verifies that unfulfilled prerequisites throw `PrerequisiteNotMetException`.
- **Course Withdrawal**: Confirms credit balance updates and seat de-allocation.
- **Waitlist Auto-Promotion**: Verifies that dropping a course promotes the first waitlisted student.
- **Facility Reservation Conflicts**: Ensures overlapping resource bookings are rejected.
- **Tuition Fee Calculation**: Verifies per-credit fee computations and payment reconciliation.
- **Stream Analytics**: Validates summary statistics and average CGPA aggregations.

---

## 8. Terminal Screenshots / Execution Sample

```
=================================================================
   SMARTCAMPUS: ACADEMIC & RESOURCE MANAGEMENT SYSTEM (VIT)   
=================================================================
 1. Student Login       (Default: student / student123)
 2. Faculty Login       (Default: prof.ananya / faculty123)
 3. Administrator Login (Default: admin / admin123)
 4. Run Automated End-to-End System Demonstration
 5. View Executive Analytics Summary
 0. Exit System
-----------------------------------------------------------------
Enter choice: 4

######################################################################
  EXECUTING SMARTCAMPUS AUTOMATED DEMONSTRATION & VERIFICATION SUITE
######################################################################

[DEMO 1] Testing Student Authentication...
 -> Student: Aarav Patel | Roll No: 24BCY10203 | Dept: Computer Science & Engineering (Cyber Security) | Sem: 3 | CGPA: 9.15 | Registered Credits: 0/27

[DEMO 2] Testing FFCS Course Registration (CSE2005 - Java OOP)...
 -> Result: Enrolled in CSE2005! Total credits: 4

[DEMO 3] Testing Slot Clash Prevention (CSE3012 is also in Slot B1)...
 -> SUCCESS: Conflict intercepted as expected: Timetable Conflict: Course 'CSE3012' (Theory Slot B1) clashes with already registered course 'CSE2005' (Theory Slot B1).

[DEMO 4] Testing Prerequisite Validation (CSE3003 requires CSE2005 completion)...
 -> SUCCESS: Prerequisite requirement enforced: Cannot register for course 'CSE3003'. Required prerequisite 'CSE2005' has not been completed.

[DEMO 5] Testing Campus Resource Reservation (Turing HPC Lab)...
 -> Requested Reservation: Booking #RES-5001: Resource RES-LAB-01 by User STU201 on 2026-09-20 Slot L1_L2 [PENDING]

[DEMO 6] Testing Admin Approval Workflow...
 -> Administrator approved booking: Status = APPROVED

[DEMO 7] Testing Financial Invoice Generation & Payment...
 -> Invoice #INV-9001 generated: Amount INR 30000.00
 -> Invoice paid! Transaction Ref: TXN-A8F1C32D (Status: PAID)

[DEMO 8] Generating Real-Time Institutional Analytics...
===============================================================
         SMARTCAMPUS EXECUTIVE ANALYTICS REPORT                
===============================================================
 Total Enrolled Students  : 2
 Total Teaching Faculty   : 3
 Active Course Offerings  : 6
 Confirmed Enrollments    : 1
 Average Student CGPA     : 9.00 / 10.00
 Campus Facility Bookings : 1
 Settled Tuition Revenue  : INR 30,000.00
 Outstanding Receivables  : INR 0.00
---------------------------------------------------------------

>>> ALL DEMONSTRATION TEST FLOWS PASSED WITH 100% SUCCESS! <<<
```

---

## 9. Submission Artifacts
- **Official Project Report (PDF)**: `docs/SmartCampus_Project_Report.pdf`
- **Official Problem Statement**: `statement.md`
- **Full Source Code**: `src/main/java/` & `src/test/java/`
- **Data & Logs**: `data/` directory with exported CSVs and audit trails.
