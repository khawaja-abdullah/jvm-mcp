package dev.jvmmcp;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "serve",
    description = "Starts the JVM-MCP server",
    mixinStandardHelpOptions = true
)
public class ServeCommand implements Runnable {

    @Option(names = "--transport", defaultValue = "stdio", description = "Transport protocol: stdio or sse")
    String transport;

    @Option(names = "--spring", description = "Use Spring AI layer (SSE, dashboard, slower startup)")
    boolean useSpring;

    @Option(names = "--attach", description = "Target PID to attach explicitly before serving")
    Integer targetPid;

    @Option(names = "--actuator", description = "Target Actuator Base URL (e.g. http://localhost:8080)")
    String actuatorUrl;

    @Override
    public void run() {
        if ("sse".equalsIgnoreCase(transport) || useSpring) {
            System.err.println("Spring layer not yet implemented. Please use stdio transport.");
            System.exit(1);
        }

        System.err.println("Starting jvm-mcp (pure SDK) via " + transport + "...");
        // TODO: Initialize io.modelcontextprotocol.sdk:mcp server here
    }
}
