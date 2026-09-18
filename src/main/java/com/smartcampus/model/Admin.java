package com.smartcampus.model;

/**
 * Academic Administrator model with high-level system permissions.
 * Demonstrates Inheritance and System Role Segregation.
 */
public class Admin extends User {
    private String adminLevel;
    private String officeLocation;

    public Admin(String id, String username, String plainPassword, String fullName, String email,
                 String adminLevel, String officeLocation) {
        super(id, username, plainPassword, fullName, email, Role.ADMIN);
        this.adminLevel = adminLevel;
        this.officeLocation = officeLocation;
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Admin: %s | Level: %s | Office: %s", getFullName(), adminLevel, officeLocation);
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
}
