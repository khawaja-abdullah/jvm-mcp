package dev.jvmmcp.core.jmx;

import dev.jvmmcp.core.model.DeadlockReport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class DeadlockDetectionTest {

    private JmxConnectionManager connectionManager;
    private ThreadMXBeanClient threadClient;
    private final AtomicBoolean running = new AtomicBoolean(true);

    @BeforeEach
    void setUp() {
        connectionManager = JmxConnectionManager.connectLocal();
        threadClient = new ThreadMXBeanClient(connectionManager.getMBeanServerConnection());
    }

    @AfterEach
    void tearDown() {
        running.set(false);
        if (connectionManager != null) {
            connectionManager.close();
        }
    }

    @Test
    @DisplayName("detectDeadlocks should accurately identify circular lock chains and owners")
    void shouldDetectCircularDeadlock() throws Exception {
        Object lockA = new Object();
        Object lockB = new Object();

        CountDownLatch thread1AcquiredLockA = new CountDownLatch(1);
        CountDownLatch thread2AcquiredLockB = new CountDownLatch(1);

        Thread thread1 = new Thread(() -> {
            synchronized (lockA) {
                thread1AcquiredLockA.countDown();
                try {
                    thread2AcquiredLockB.await(5, TimeUnit.SECONDS);
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
                synchronized (lockB) {
                    while (running.get()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ignored) {}
                    }
                }
            }
        }, "Deadlock-Worker-1");

        Thread thread2 = new Thread(() -> {
            synchronized (lockB) {
                thread2AcquiredLockB.countDown();
                try {
                    thread1AcquiredLockA.await(5, TimeUnit.SECONDS);
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
                synchronized (lockA) {
                    while (running.get()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ignored) {}
                    }
                }
            }
        }, "Deadlock-Worker-2");

        thread1.setDaemon(true);
        thread2.setDaemon(true);

        thread1.start();
        thread2.start();

        // Wait until both threads are blocked waiting for each other's locks
        boolean acquired = thread1AcquiredLockA.await(5, TimeUnit.SECONDS) && thread2AcquiredLockB.await(5, TimeUnit.SECONDS);
        assertThat(acquired).isTrue();

        // Allow time for the threads to transition to BLOCKED state
        Thread.sleep(300);

        DeadlockReport report = threadClient.detectDeadlocks();

        assertThat(report).isNotNull();
        assertThat(report.status()).isEqualTo("DETECTED");
        assertThat(report.deadlockCount()).isGreaterThanOrEqualTo(2);
        assertThat(report.chain()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(report.recommendation()).contains("Deadlock detected");

        boolean containsThread1 = report.chain().stream().anyMatch(d -> "Deadlock-Worker-1".equals(d.threadName()));
        boolean containsThread2 = report.chain().stream().anyMatch(d -> "Deadlock-Worker-2".equals(d.threadName()));

        assertThat(containsThread1).isTrue();
        assertThat(containsThread2).isTrue();
    }
}
