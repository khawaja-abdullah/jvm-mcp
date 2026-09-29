package dev.jvmmcp.core.model;

import java.time.Instant;
import java.util.List;

/**
 * Complete thread dump snapshot of a target JVM process.
 */
public record ThreadDump(
    long pid,
    Instant timestamp,
    int totalCount,
    List<ThreadDetail> threads
) {}
