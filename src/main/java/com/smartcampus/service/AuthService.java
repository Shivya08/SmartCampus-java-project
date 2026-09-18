package com.smartcampus.service;

import com.smartcampus.concurrency.AsyncAuditLogger;
import com.smartcampus.exception.AuthenticationException;
import com.smartcampus.model.Role;
import com.smartcampus.model.User;
import com.smartcampus.repository.InMemoryDataStore;

import java.util.Optional;

/**
 * Authentication and Session Management Service.
 * Implements secure login validation, credential hashing, and audit tracking.
 */
public class AuthService {
    private final InMemoryDataStore dataStore;
    private final AsyncAuditLogger logger;
    private User currentUser;

    public AuthService() {
        this.dataStore = InMemoryDataStore.getInstance();
        this.logger = AsyncAuditLogger.getInstance();
    }

    public User login(String username, String plainPassword) throws AuthenticationException {
        if (username == null || plainPassword == null) {
            throw new AuthenticationException("Username and password cannot be null");
        }

        User user = dataStore.getUsers().get(username.toLowerCase().trim());
        if (user == null || !user.isActive()) {
            logger.logSecurity("ANONYMOUS", "LOGIN_FAILED", "Unknown user or deactivated account: " + username);
            throw new AuthenticationException("Invalid username or account is deactivated");
        }

        if (!user.checkPassword(plainPassword)) {
            logger.logSecurity(user.getId(), "LOGIN_FAILED", "Incorrect password attempt for user: " + username);
            throw new AuthenticationException("Invalid password credentials");
        }

        this.currentUser = user;
        logger.logSecurity(user.getId(), "LOGIN_SUCCESS", "User authenticated successfully as " + user.getRole());
        return user;
    }

    public void logout() {
        if (currentUser != null) {
            logger.logInfo(currentUser.getId(), "LOGOUT", "User logged out of session");
            currentUser = null;
        }
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void requireRole(Role expectedRole) throws AuthenticationException {
        if (currentUser == null) {
            throw new AuthenticationException("Authentication required. Please log in first.");
        }
        if (currentUser.getRole() != expectedRole) {
            logger.logSecurity(currentUser.getId(), "ACCESS_DENIED",
                    String.format("Requires %s, but user has %s", expectedRole, currentUser.getRole()));
            throw new AuthenticationException(
                    String.format("Unauthorized: Action requires %s role privileges.", expectedRole));
        }
    }
}
