# Project Problem Statement: SmartCampus

## 1. Problem Statement
At VIT, we follow the Fully Flexible Credit System (FFCS), which gives students the freedom to choose their own subjects, class timings (slots), and teachers. While this flexibility is great, students and staff face several practical issues every semester:

1. **Slot Clashes:** When making a timetable, students often accidentally register for two courses that have overlapping timings or are in the exact same slot (like having two classes in slot B1). This creates confusion and attendance issues later.
2. **Missing Prerequisites:** Some students try to take higher-level courses (like Operating Systems) without having cleared the necessary foundation course (like Object Oriented Programming in Java).
3. **Waitlist Issues:** When popular courses fill up, students get blocked. If someone drops a course later, that seat is not always given fairly to the student who was next in line.
4. **Room and Lab Booking Conflicts:** Booking shared campus facilities like computer labs, project rooms, and seminar halls often happens manually, leading to two groups trying to use the same room at the same time.
5. **Slow System Performance:** Saving every single student action directly to files or databases during busy registration hours can slow the whole system down.

**SmartCampus** is a Java-based project created to solve these problems by automating course registration, checking for timetable conflicts in real time, handling room bookings, managing waitlists, and processing background logs efficiently.

---

## 2. Scope of the Project
The scope of this project includes:
- **User Logins:** Secure login system with roles for Students, Faculty, and Administrators using SHA-256 password hashing.
- **Course Registration Engine:** Checks for slot clashes, prerequisite completion, and the 27-credit limit per semester.
- **Automatic Waitlist Management:** When someone drops a full course, the first student on the waitlist is automatically enrolled.
- **Facility Booking:** Requesting and approving reservations for labs, seminar halls, and study pods without overlap.
- **Fee Billing:** Generating fee receipts based on total registered credits.
- **Analytics:** Using Java Streams to calculate average CGPA, student distribution across departments, and faculty workloads.
- **Background Logging:** Using a multithreaded queue to log user actions in the background without freezing the UI.
- **File Storage:** Saving records and exporting data to CSV files.

---

## 3. Target Users
- **Students:** To check the course catalog, build their timetable without clashes, book study rooms, and pay fees.
- **Faculty Members:** To check their assigned teaching subjects, view student rosters, and book seminar halls for talks.
- **Academic Administrators:** To review room booking requests, oversee course capacities, view university analytics, and export student records.

---

## 4. Key Features
- Instant slot clash warning before course registration is confirmed.
- Automatic prerequisite validation.
- FIFO waitlist promotion when seats open up.
- Clean console menus with clear tables for easy navigation.
- Asynchronous logging using Java threads for fast response times.
- Zero external dependencies required to compile or run.
