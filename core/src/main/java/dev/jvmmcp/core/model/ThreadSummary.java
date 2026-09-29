package dev.jvmmcp.core.model;

/**
 * High-level breakdown of JVM thread states and counts.
 */
public record ThreadSummary(
    long pid,
    int totalCount,
    int daemonCount,
    int peakCount,
    long totalStartedCount,
    int runnableCount,
    int blockedCount,
    int waitingCount,
    int timedWaitingCount
) {}
