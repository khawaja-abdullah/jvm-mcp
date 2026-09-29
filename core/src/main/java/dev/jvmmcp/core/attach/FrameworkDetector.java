package dev.jvmmcp.core.attach;

import dev.jvmmcp.core.model.Framework;

/**
 * Heuristic detector for identifying common JVM frameworks from process metadata.
 */
public final class FrameworkDetector {

    private FrameworkDetector() {}

    public static Framework detect(String displayName, String mainClass) {
        String combined = ((displayName != null ? displayName : "") + " " + (mainClass != null ? mainClass : "")).toLowerCase();

        if (combined.contains("org.springframework.boot") || combined.contains("springapplication") || combined.contains("springboot") || combined.contains("spring-boot")) {
            return Framework.SPRING_BOOT;
        }
        if (combined.contains("io.quarkus") || combined.contains("quarkus")) {
            return Framework.QUARKUS;
        }
        if (combined.contains("io.micronaut") || combined.contains("micronaut")) {
            return Framework.MICRONAUT;
        }
        if (displayName != null && !displayName.isBlank()) {
            return Framework.PLAIN_JAVA;
        }
        return Framework.UNKNOWN;
    }
}
