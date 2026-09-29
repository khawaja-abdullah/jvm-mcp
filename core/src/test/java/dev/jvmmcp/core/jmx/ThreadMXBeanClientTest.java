package dev.jvmmcp.core.jmx;

import dev.jvmmcp.core.model.DeadlockReport;
import dev.jvmmcp.core.model.ThreadDump;
import dev.jvmmcp.core.model.ThreadSummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadMXBeanClientTest {

    private JmxConnectionManager connectionManager;
    private ThreadMXBeanClient threadClient;

    @BeforeEach
    void setUp() {
        connectionManager = JmxConnectionManager.connectLocal();
        threadClient = new ThreadMXBeanClient(connectionManager.getMBeanServerConnection());
    }

    @AfterEach
    void tearDown() {
        if (connectionManager != null) {
            connectionManager.close();
        }
    }

    @Test
    @DisplayName("getThreadSummary should return accurate state counts for current JVM")
    void shouldExtractValidThreadSummary() throws IOException {
        long currentPid = ProcessHandle.current().pid();
        ThreadSummary summary = threadClient.getThreadSummary(currentPid);

        assertThat(summary).isNotNull();
        assertThat(summary.pid()).isEqualTo(currentPid);
        assertThat(summary.totalCount()).isPositive();
        assertThat(summary.runnableCount()).isPositive();
        assertThat(summary.peakCount()).isGreaterThanOrEqualTo(summary.totalCount());
    }

    @Test
    @DisplayName("getThreadDump should capture formatted thread stack frames")
    void shouldCaptureStructuredThreadDump() throws IOException {
        long currentPid = ProcessHandle.current().pid();
        ThreadDump dump = threadClient.getThreadDump(currentPid);

        assertThat(dump).isNotNull();
        assertThat(dump.pid()).isEqualTo(currentPid);
        assertThat(dump.threads()).isNotEmpty();

        boolean hasStackTrace = dump.threads().stream().anyMatch(t -> !t.stackTrace().isEmpty());
        assertThat(hasStackTrace).isTrue();
    }

    @Test
    @DisplayName("detectDeadlocks should return NONE report when no threads are deadlocked")
    void shouldReportNoDeadlocksOnCleanJvm() throws IOException {
        DeadlockReport report = threadClient.detectDeadlocks();

        assertThat(report).isNotNull();
        assertThat(report.status()).isEqualTo("NONE");
        assertThat(report.deadlockCount()).isZero();
        assertThat(report.chain()).isEmpty();
    }
}
