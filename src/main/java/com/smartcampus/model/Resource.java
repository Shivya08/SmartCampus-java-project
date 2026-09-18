package com.smartcampus.model;

/**
 * Resource model representing bookable campus infrastructure such as computing laboratories,
 * seminar auditoriums, smart conference rooms, and collaborative study pods.
 */
public class Resource {
    public enum ResourceType {
        LABORATORY,
        SEMINAR_HALL,
        STUDY_POD,
        SMART_CLASSROOM
    }

    private final String resourceId;
    private String name;
    private ResourceType type;
    private int capacity;
    private String location;
    private boolean available;

    public Resource(String resourceId, String name, ResourceType type, int capacity, String location) {
        this.resourceId = resourceId;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.location = location;
        this.available = true;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, Cap: %d, Loc: %s)",
                resourceId, name, type, capacity, location);
    }
}
