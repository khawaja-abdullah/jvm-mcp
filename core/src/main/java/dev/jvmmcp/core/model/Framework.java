package dev.jvmmcp.core.model;

public enum Framework {
    SPRING_BOOT("Spring Boot"),
    QUARKUS("Quarkus"),
    MICRONAUT("Micronaut"),
    PLAIN_JAVA("Plain Java"),
    UNKNOWN("Unknown");

    private final String displayName;

    Framework(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
