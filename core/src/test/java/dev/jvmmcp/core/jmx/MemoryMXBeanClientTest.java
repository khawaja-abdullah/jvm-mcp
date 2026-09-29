package dev.jvmmcp.core.jmx;

import dev.jvmmcp.core.model.HeapSummary;
import dev.jvmmcp.core.model.MemoryPressure;
import dev.jvmmcp.core.model.MemoryPressureLevel;
import dev.jvmmcp.core.model.MemoryUsageInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryMXBeanClientTest {

    private JmxConnectionManager connectionManager;
    private MemoryMXBeanClient memoryClient;

    @BeforeEach
    void setUp() {
        connectionManager = JmxConnectionManager.connectLocal();
        memoryClient = new MemoryMXBeanClient(connectionManager.getMBeanServerConnection());
    }

    @AfterEach
    void tearDown() {
        if (connectionManager != null) {
            connectionManager.close();
        }
    }

    @Test
    @DisplayName("getHeapSummary should extract valid live memory and GC metrics from current JVM")
    void shouldExtractValidHeapSummary() throws IOException {
        long currentPid = ProcessHandle.current().pid();
        HeapSummary summary = memoryClient.getHeapSummary(currentPid);

        assertThat(summary).isNotNull();
        assertThat(summary.pid()).isEqualTo(currentPid);
        assertThat(summary.heap().usedBytes()).isPositive();
        assertThat(summary.heap().committedBytes()).isPositive();
        assertThat(summary.pools()).isNotEmpty();
        assertThat(summary.garbageCollectors()).isNotEmpty();
        assertThat(summary.pressure()).isNotNull();
        assertThat(summary.pressure().level()).isNotNull();
    }

    @Test
    @DisplayName("evaluatePressure should flag critical when heap ratio exceeds 95%")
    void shouldFlagCriticalPressure() {
        MemoryUsageInfo criticalUsage = new MemoryUsageInfo(100, 960, 1000, 1000, 96.0, 96.0, 100.0);
        MemoryPressure pressure = memoryClient.evaluatePressure(criticalUsage, List.of());

        assertThat(pressure.level()).isEqualTo(MemoryPressureLevel.CRITICAL);
        assertThat(pressure.recommendation()).contains("CRITICAL");
    }

    @Test
    @DisplayName("evaluatePressure should flag normal when heap ratio is low")
    void shouldFlagNormalPressure() {
        MemoryUsageInfo normalUsage = new MemoryUsageInfo(100, 200, 1000, 1000, 20.0, 20.0, 100.0);
        MemoryPressure pressure = memoryClient.evaluatePressure(normalUsage, List.of());

        assertThat(pressure.level()).isEqualTo(MemoryPressureLevel.NORMAL);
        assertThat(pressure.recommendation()).contains("NORMAL");
    }
}
