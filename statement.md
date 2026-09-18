# Project Statement: SmartCampus

## 1. Problem Statement
Higher education institutions like Vellore Institute of Technology (VIT) operate on dynamic, student-centric academic paradigms such as the Fully Flexible Credit System (FFCS). In large-scale academic ecosystems, students face several recurring bottlenecks:
- **Timetable Slot Clashes**: Accidental selection of conflicting courses across theoretical and laboratory slots leading to academic disorientation.
- **Prerequisite Violations**: Attempting to register for advanced courses without mastering prerequisite foundation subjects.
- **Decentralized Resource Reservation**: Campus physical infrastructure (high-performance compute labs, seminar auditoriums, research pods) is often managed manually or through siloed spreadsheets, creating scheduling collisions and underutilization.
- **Opaque Financial Reconciliation**: Disconnected credit billing mechanisms where tuition fees, laboratory fees, and campus facility charges fail to synchronize transparently with student enrollment tallies.
- **Audit & Compliance Gaps**: Absence of thread-safe, asynchronous transaction auditing to log enrollment adjustments, seat re-allocations, and administrative decisions.

**SmartCampus** addresses these challenges by delivering an integrated, high-concurrency, modular Java application that models university academic life, enforces FFCS constraints, handles automated waitlist promotion, automates resource scheduling, and tracks financial accounting with background audit trails.

---

## 2. Scope of the Project
The scope of SmartCampus covers:
- **Role-Based Identity & Security**: Granular access control for Students, Faculty Members, and Academic Administrators with cryptographic password hashing.
- **FFCS Course Registration Engine**: Intelligent enrollment with real-time slot clash detection, prerequisite checking, credit ceiling enforcement (max 27 credits), and automated waitlist promotion upon student withdrawal.
- **Campus Facility Reservation System**: Centralized scheduling of computing labs, auditoriums, and collaborative pods with conflict prevention and administrative approval workflows.
- **Financial Ledger & Billing**: Automatic per-credit tuition fee calculation, invoice issuance, and payment receipt reconciliation.
- **Institutional Analytics Engine**: Modern Java Stream API-powered analytics calculating GPA metrics, departmental distributions, faculty workload allocations, and slot occupancy.
- **High-Performance Asynchronous Auditing**: Producer-consumer multithreaded logging using non-blocking queues to ensure transaction integrity without stalling system throughput.
- **Persistent Data Management**: Disk persistence using Java NIO.2 and formatted CSV exports for institutional data interoperability.

---

## 3. Target Users
1. **University Students**:
   - Register for semester courses under FFCS rules.
   - Inspect weekly schedules and credit tallies.
   - Reserve campus research labs and collaborative pods.
   - Review tuition fee invoices and settle educational dues.
2. **Faculty Members**:
   - Monitor assigned course rosters, active student enrollments, and syllabus distribution.
   - Reserve smart classrooms and seminar auditoriums for guest lectures and conferences.
3. **Academic Administrators & Registrars**:
   - Oversee university-wide course offerings and enrollment quotas.
   - Approve or reject facility reservation requests with administrative rationale.
   - Trigger batch invoice generation for semester credits.
   - Analyze institutional metrics and inspect real-time security audit trails.

---

## 4. High-Level Features
- **FFCS Intelligent Slot Clash Interceptor**: Mathematical overlap checking across lecture days and time windows.
- **Automated Waitlist Queue Promotion**: Dynamic FIFO queue re-allocation when seats open up.
- **Multi-Role Console User Interface**: Intuitive, table-formatted CLI dashboards.
- **Asynchronous Concurrent Audit Trail**: Thread-safe background logging with zero impact on UI responsiveness.
- **Stream-Driven Institutional Intelligence**: Real-time statistical metrics with Java Streams and Lambdas.
- **Zero-Dependency Compilation & Execution**: Ready to run out of the box with standard Java and Maven support.
