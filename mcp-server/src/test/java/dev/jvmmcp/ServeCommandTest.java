package dev.jvmmcp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class ServeCommandTest {

    @Test
    @DisplayName("serve command with --help should display options and return exit code 0")
    void shouldDisplayHelp() {
        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(new JvmMcp());
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("serve", "--help");

        assertThat(exitCode).isZero();
        assertThat(out.toString()).contains("Starts the JVM-MCP server");
        assertThat(out.toString()).contains("--transport");
        assertThat(out.toString()).contains("--spring");
        assertThat(out.toString()).contains("--attach");
    }

    @Test
    @DisplayName("serve command with unimplemented sse transport should return exit code 1")
    void shouldFailOnUnimplementedSseTransport() {
        CommandLine cmd = new CommandLine(new JvmMcp());
        int exitCode = cmd.execute("serve", "--transport", "sse");

        assertThat(exitCode).isEqualTo(1);
    }

    @Test
    @DisplayName("serve command with stdio transport and valid options should return exit code 0")
    void shouldAcceptStdioTransport() {
        CommandLine cmd = new CommandLine(new JvmMcp());
        int exitCode = cmd.execute("serve", "--transport", "stdio");

        assertThat(exitCode).isZero();
    }
}
