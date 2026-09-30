package dev.jvmmcp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadsCommandTest {

    @Test
    @DisplayName("threads command with --help should display options and return exit code 0")
    void shouldDisplayHelp() {
        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(new JvmMcp());
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("threads", "--help");

        assertThat(exitCode).isZero();
        assertThat(out.toString()).contains("Inspects JVM thread states");
        assertThat(out.toString()).contains("--deadlocks");
        assertThat(out.toString()).contains("--dump");
        assertThat(out.toString()).contains("--blocked");
    }

    @Test
    @DisplayName("threads command with invalid/missing PID should return error exit code")
    void shouldFailOnMissingOrInvalidPid() {
        CommandLine cmd = new CommandLine(new JvmMcp());

        int noPidExit = cmd.execute("threads");
        int negativePidExit = cmd.execute("threads", "-1");

        assertThat(noPidExit).isNotZero();
        assertThat(negativePidExit).isNotZero();
    }

    @Test
    @DisplayName("threads command with non-existent PID should return error exit code")
    void shouldFailGracefullyOnNonExistentPid() {
        CommandLine cmd = new CommandLine(new JvmMcp());
        int exitCode = cmd.execute("threads", "999999999");

        assertThat(exitCode).isEqualTo(1);
    }
}
