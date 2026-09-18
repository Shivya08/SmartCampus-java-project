package com.smartcampus.util;

import java.util.regex.Pattern;

/**
 * Input validation utility for email addresses, roll numbers, and string formatting.
 */
public class Validator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern ROLL_NUMBER_PATTERN =
            Pattern.compile("^[0-9]{2}[A-Za-z]{3}[0-9]{4,5}$");

    private Validator() {}

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidRollNumber(String rollNumber) {
        if (rollNumber == null) return false;
        return ROLL_NUMBER_PATTERN.matcher(rollNumber.trim().toUpperCase()).matches();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
