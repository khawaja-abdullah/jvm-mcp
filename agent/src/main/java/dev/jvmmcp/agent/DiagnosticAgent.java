package dev.jvmmcp.agent;

import java.lang.instrument.Instrumentation;

/**
 * Dynamically loaded Java Agent.
 * Injected into the target JVM via the Attach API to expose JMX metrics 
 * if the target JVM lacks local JMX connections.
 */
public class DiagnosticAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        init(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        init(agentArgs, inst);
    }

    private static void init(String agentArgs, Instrumentation inst) {
        System.out.println("[jvm-mcp] Diagnostic agent loaded successfully.");
        // Future: Start local JMX connector or register custom MBeans
    }
}
