package dev.jvmmcp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryCommandTest {

    @Test
    @DisplayName("memory command with --help should display options and return exit code 0")
    void shouldDisplayHelp() {
        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(new JvmMcp());
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("memory", "--help");

        assertThat(exitCode).isZero();
        assertThat(out.toString()).contains("Inspects JVM heap memory");
        assertThat(out.toString()).contains("--histogram");
        assertThat(out.toString()).contains("--top");
    }

    @Test
    @DisplayName("memory command with invalid/missing PID should return error exit code")
    void shouldFailOnMissingOrInvalidPid() {
        CommandLine cmd = new CommandLine(new JvmMcp());

        int noPidExit = cmd.execute("memory");
        int negativePidExit = cmd.execute("memory", "-1");

        assertThat(noPidExit).isNotZero();
        assertThat(negativePidExit).isNotZero();
    }

    @Test
    @DisplayName("memory command with non-existent PID should return error exit code")
    void shouldFailGracefullyOnNonExistentPid() {
        CommandLine cmd = new CommandLine(new JvmMcp());
        int exitCode = cmd.execute("memory", "999999999");

        assertThat(exitCode).isEqualTo(1);
    }
}
