package dev.jvmmcp.core.model;

import java.lang.management.MemoryUsage;

/**
 * Immutable memory usage snapshot formatted for programmatic and LLM consumption.
 */
public record MemoryUsageInfo(
    long initBytes,
    long usedBytes,
    long committedBytes,
    long maxBytes,
    double usedPercent,
    double usedMb,
    double maxMb
) {
    public static MemoryUsageInfo from(MemoryUsage usage) {
        if (usage == null) {
            return new MemoryUsageInfo(0, 0, 0, 0, 0.0, 0.0, 0.0);
        }
        long init = usage.getInit();
        long used = usage.getUsed();
        long committed = usage.getCommitted();
        long max = usage.getMax();
        double usedPercent = max > 0 
            ? ((double) used / (double) max) * 100.0 
            : (committed > 0 ? ((double) used / (double) committed) * 100.0 : 0.0);
        double usedMb = used / (1024.0 * 1024.0);
        double maxMb = max > 0 ? max / (1024.0 * 1024.0) : committed / (1024.0 * 1024.0);

        return new MemoryUsageInfo(
            init,
            used,
            committed,
            max,
            Math.round(usedPercent * 100.0) / 100.0,
            Math.round(usedMb * 100.0) / 100.0,
            Math.round(maxMb * 100.0) / 100.0
        );
    }
}
