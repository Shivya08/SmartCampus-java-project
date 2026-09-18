package com.smartcampus.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

/**
 * Abstract base class representing a generic user in the SmartCampus system.
 * Demonstrates the Object-Oriented principles of Abstraction and Encapsulation.
 */
public abstract class User {
    private final String id;
    private final String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private final Role role;
    private final LocalDateTime createdAt;
    private boolean active;

    public User(String id, String username, String plainPassword, String fullName, String email, Role role) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.id = id;
        this.username = username.toLowerCase().trim();
        this.passwordHash = hashPassword(plainPassword);
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    /**
     * Polymorphic method implemented by each subclass to provide tailored dashboard details.
     */
    public abstract String getDashboardSummary();

    /**
     * Validates an entered plain text password against the stored cryptographic hash.
     */
    public boolean checkPassword(String plainPassword) {
        if (plainPassword == null) return false;
        return this.passwordHash.equals(hashPassword(plainPassword));
    }

    public void updatePassword(String newPlainPassword) {
        if (newPlainPassword == null || newPlainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        this.passwordHash = hashPassword(newPlainPassword);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", role, fullName, username, email);
    }
}
