package com.smartcampus;

import com.smartcampus.exception.CampusException;
import com.smartcampus.exception.CreditLimitExceededException;
import com.smartcampus.exception.PrerequisiteNotMetException;
import com.smartcampus.exception.SlotClashException;
import com.smartcampus.model.Course;
import com.smartcampus.model.Registration;
import com.smartcampus.model.Student;
import com.smartcampus.repository.InMemoryDataStore;
import com.smartcampus.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for FFCS Course Registration logic.
 */
public class RegistrationServiceTest {
    private RegistrationService registrationService;
    private InMemoryDataStore dataStore;

    @BeforeEach
    public void setUp() {
        this.registrationService = new RegistrationService();
        this.dataStore = InMemoryDataStore.getInstance();
    }

    @Test
    @DisplayName("Should successfully enroll a student when prerequisites and slots are clear")
    public void testSuccessfulRegistration() throws CampusException {
        Student student = (Student) dataStore.getUsers().get("student");
        assertNotNull(student);

        Registration reg = registrationService.registerCourse(student.getId(), "CSE2005");
        assertNotNull(reg);
        assertEquals("CSE2005", reg.getCourseCode());
        assertEquals(Registration.Status.REGISTERED, reg.getStatus());
        assertTrue(student.getRegisteredCourseCodes().contains("CSE2005"));
    }

    @Test
    @DisplayName("Should reject course registration if time slot clashes with an enrolled course")
    public void testSlotClashDetection() throws CampusException {
        Student student = (Student) dataStore.getUsers().get("student");
        assertNotNull(student);

        // CSE2005 is in Slot B1
        if (!student.getRegisteredCourseCodes().contains("CSE2005")) {
            registrationService.registerCourse(student.getId(), "CSE2005");
        }

        // CSE3012 is also in Slot B1 -> Should throw SlotClashException
        assertThrows(SlotClashException.class, () -> {
            registrationService.registerCourse(student.getId(), "CSE3012");
        });
    }

    @Test
    @DisplayName("Should reject course registration if mandatory prerequisite is not completed")
    public void testPrerequisiteEnforcement() {
        Student student = (Student) dataStore.getUsers().get("student");
        assertNotNull(student);

        // CSE3003 requires CSE2005 to be completed
        assertThrows(PrerequisiteNotMetException.class, () -> {
            registrationService.registerCourse(student.getId(), "CSE3003");
        });
    }

    @Test
    @DisplayName("Should correctly handle course withdrawal and credit reduction")
    public void testDropCourse() throws CampusException {
        Student student = (Student) dataStore.getUsers().get("student");
        if (!student.getRegisteredCourseCodes().contains("CSE2005")) {
            registrationService.registerCourse(student.getId(), "CSE2005");
        }

        int creditsBefore = student.getCurrentCredits();
        boolean dropped = registrationService.dropCourse(student.getId(), "CSE2005");
        assertTrue(dropped);
        assertFalse(student.getRegisteredCourseCodes().contains("CSE2005"));
        assertEquals(creditsBefore - 4, student.getCurrentCredits());
    }
}
