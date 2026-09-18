package com.smartcampus.exception;

/**
 * Thrown when an entity lookup (student, course, resource, invoice) yields no results.
 */
public class EntityNotFoundException extends CampusException {
    private final String entityType;
    private final String identifier;

    public EntityNotFoundException(String entityType, String identifier) {
        super("ENTITY_NOT_FOUND",
                String.format("%s with identifier '%s' was not found.", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getIdentifier() {
        return identifier;
    }
}
