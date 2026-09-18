# SmartCampus: University Course Registration & Campus Management System

**Course:** Object Oriented Programming with Java (CSE2005)  
**Institution:** Vellore Institute of Technology (VIT)  
**Author:** Shivya (Reg No: 24BCY10203)  
**Submission:** VITyarthi Flipped Course Project  

---

## What is this project?

In universities like VIT, course registration follows the Fully Flexible Credit System (FFCS). Students pick their own slots, courses, and faculties. During registration, things often get messy because:
- People accidentally pick courses in the same slot (like two theory courses in B1).
- Students try taking advanced courses without completing the prerequisites.
- When popular courses fill up, seats that get dropped later don't always go to the people waiting in line.
- Booking computer labs or project rooms usually happens on paper or random forms, which leads to two people booking the same room at the same time.

I built **SmartCampus** in Java to solve these exact problems. It is a console application that handles course registration, clash detection, waitlists, room booking, fee calculations, and background logging.

---

## What the system does (Features)

### 1. User Logins (Student, Faculty, Admin)
- Passwords are encrypted using SHA-256 before being checked.
- Different menus for students, teachers, and admins.
- Each user type sees a customized dashboard when they log in.

### 2. FFCS Course Registration & Clash Checks
- View the catalog of offered courses, credit counts, faculty names, and remaining seats.
- **Clash Detector:** If a student tries to register for a course that has the same day and time as an already registered course, the system blocks it and shows an error message explaining the clash.
- **Prerequisite Check:** If a course requires an earlier course (e.g., Operating Systems requires OOP in Java), the system checks if you passed it first.
- **Credit Limit:** Caps registrations at 27 credits per semester.
- **Auto-Waitlist:** If a course is full, you are added to a waitlist. When another student drops that course, the first student on the waitlist is automatically promoted and enrolled.

### 3. Campus Facility Booking
- Students and teachers can request rooms (computer lab, seminar hall, library study pod).
- The system checks dates and slots so no two bookings overlap.
- Admins can log in to approve or reject pending requests.

### 4. Fee Calculation & Invoicing
- Calculates tuition based on registered credits (4,500 rupees per credit + lab fee + campus fee).
- Generates an invoice that students can view and mark as paid.
- Creates a transaction reference code when paid.

### 5. Fast Background Logging (Multithreading)
- Instead of slowing down user clicks by saving logs to disk immediately, actions are sent to an in-memory queue. A background worker thread saves them to a log file quietly without freezing the screen.

### 6. Reports & Analytics
- Uses Java Streams to calculate average CGPA, most popular courses, and department distribution.

---

## Tech Stack Used

- **Language:** Java (JDK 17)
- **Concepts Applied:** OOP (Inheritance, Polymorphism, Encapsulation, Abstract classes), Custom Exceptions, Collections (`ConcurrentHashMap`, `LinkedList`, `PriorityQueue`), Multithreading (`BlockingQueue`, `ExecutorService`), Streams & Lambdas, File I/O (`java.nio`).
- **Build / Run:** Windows batch files (`run.bat`, `build.bat`) and Maven (`pom.xml`).
- **Testing:** Custom test runner class + JUnit 5 tests.

---

## Project Structure

```
SmartCampus/
├── src/main/java/com/smartcampus/
│   ├── model/         # User, Student, Faculty, Admin, Course, TimeSlot, Resource, Invoice
│   ├── service/       # RegistrationService (clash & waitlist logic), AuthService, Billing, etc.
│   ├── repository/    # In-memory storage & FilePersistenceManager (CSV export)
│   ├── concurrency/   # AsyncAuditLogger (producer-consumer background thread)
│   ├── exception/     # SlotClashException, PrerequisiteNotMetException, etc.
│   ├── cli/           # Console menus and table formatting
│   └── Main.java      # Program entry point
├── src/test/java/     # Test classes and StandaloneTestRunner
├── docs/              # Project report (.docx, .pdf, .md)
├── pom.xml            # Maven configuration
├── build.bat          # Compile script for Windows
├── run.bat            # Run script for Windows
├── statement.md       # Problem statement file for submission
└── README.md          # Project readme
```

---

## How to Install and Run

### Running on Windows (Easiest way)
1. Open Command Prompt or PowerShell in this folder:
   ```cmd
   cd C:\Users\hp\.gemini\antigravity\scratch\SmartCampus
   ```
2. Run the application:
   ```cmd
   .\run.bat
   ```
   *(This will automatically compile the code and start the interactive menu).*

3. Or run the automated demo:
   ```cmd
   .\run.bat --demo
   ```

### Running with Maven
If you use Maven:
```bash
mvn clean compile package
java -jar target/smartcampus-core-1.0.0.jar
```

---

## Login Credentials to Test

You can log in with any of these pre-seeded accounts:

| Role | Username | Password | Details |
| :--- | :--- | :--- | :--- |
| **Student** | `student` | `student123` | Aarav Patel (3rd Sem, 9.15 CGPA) |
| **Student** | `priya.nair` | `student123` | Priya Nair (3rd Sem, 8.84 CGPA) |
| **Faculty** | `prof.ananya` | `faculty123` | Dr. Ananya Sharma (CSE Professor) |
| **Admin** | `admin` | `admin123` | Academic Dean Account |

---

## How Testing Works

To run the automated tests, run:
```cmd
.\run.bat --test
```

This runs `StandaloneTestRunner.java`, which verifies 8 different test cases:
1. Normal course registration works.
2. Registering two courses in the same slot throws `SlotClashException`.
3. Registering without prerequisites throws `PrerequisiteNotMetException`.
4. Dropping a course deducts credits and frees up the seat.
5. Facility booking works and creates a pending request.
6. Double-booking the same room on the same slot is blocked.
7. Tuition fee calculation and invoice payment marks status as paid.
8. Stream analytics returns accurate values.

All 8 tests pass with a 100% success rate.

---

## Sample Program Output

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
 Total Enrolled Students  : 2
 Total Teaching Faculty   : 3
 Active Course Offerings  : 6
 Average Student CGPA     : 9.00 / 10.00
 Settled Tuition Revenue  : INR 30,000.00

>>> ALL DEMONSTRATION TEST FLOWS PASSED WITH 100% SUCCESS! <<<
```
