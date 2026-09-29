package dev.jvmmcp.core.model;

import java.util.List;

/**
 * Diagnostic record representing a thread currently in BLOCKED state waiting on a monitor lock.
 */
public record BlockedThreadDetail(
    long threadId,
    String threadName,
    long blockedTimeMs,
    long blockedCount,
    String lockName,
    Long lockOwnerId,
    String lockOwnerName,
    List<ThreadStackFrame> stackTrace
) {}
