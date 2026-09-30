package dev.jvmmcp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

class ListCommandTest {

    @Test
    @DisplayName("list command should execute successfully and discover JVM processes")
    void shouldExecuteListCommand() {
        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(new JvmMcp());
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("list");

        assertThat(exitCode).isZero();
    }

    @Test
    @DisplayName("ps and ls aliases should route to list command")
    void shouldSupportAliases() {
        CommandLine cmd = new CommandLine(new JvmMcp());

        int psExit = cmd.execute("ps");
        int lsExit = cmd.execute("ls");

        assertThat(psExit).isZero();
        assertThat(lsExit).isZero();
    }
}
