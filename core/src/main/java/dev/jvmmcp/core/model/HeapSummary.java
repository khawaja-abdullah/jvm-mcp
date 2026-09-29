package dev.jvmmcp.core.model;

import java.util.List;

/**
 * Comprehensive memory diagnostic summary of a target JVM process.
 */
public record HeapSummary(
    long pid,
    MemoryUsageInfo heap,
    MemoryUsageInfo nonHeap,
    List<MemoryPoolInfo> pools,
    List<GarbageCollectorInfo> garbageCollectors,
    MemoryPressure pressure
) {}
