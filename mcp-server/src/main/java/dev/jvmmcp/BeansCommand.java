package dev.jvmmcp;

import dev.jvmmcp.core.attach.AttachResult;
import dev.jvmmcp.core.attach.JvmAttachService;
import dev.jvmmcp.core.jmx.JmxConnectionManager;
import dev.jvmmcp.core.model.SpringBeanDetail;
import dev.jvmmcp.core.model.SpringBeansReport;
import dev.jvmmcp.core.model.SpringContextBeans;
import dev.jvmmcp.core.spring.SpringBeansClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.Optional;

@Command(
    name = "beans",
    aliases = {"bean", "spring-beans", "sb"},
    description = "Inspects live Spring Boot ApplicationContext beans, scopes, types, and dependency graphs",
    mixinStandardHelpOptions = true
)
public class BeansCommand implements Runnable {

    @Parameters(index = "0", defaultValue = "0", description = "Target JVM Process ID (PID). Omit if using --actuator.")
    long pid;

    @Option(names = {"--filter", "-f"}, description = "Filter beans by name or type using glob patterns (e.g. '*Service*', '*Repository*')")
    String filter;

    @Option(names = {"--detail", "-d"}, description = "Show deep inspection of a specific bean by name or alias")
    String detailBeanName;

    @Option(names = {"--actuator", "-a"}, description = "Direct Actuator Base URL (e.g. 'http://localhost:8080')")
    String actuatorUrl;

    @Override
    public void run() {
        SpringBeansClient client = new SpringBeansClient();
        SpringBeansReport report;

        if (actuatorUrl != null && !actuatorUrl.isBlank()) {
            report = client.inspectFromActuatorUrl(pid, actuatorUrl, filter);
        } else {
            if (pid <= 0) {
                System.err.println("[jvm-mcp] Error: Specify a valid target PID or pass --actuator <url>");
                System.exit(1);
                return;
            }

            JvmAttachService attachService = new JvmAttachService();
            AttachResult attachResult = attachService.attach(String.valueOf(pid));

            if (!attachResult.isSuccessful()) {
                System.err.println("[jvm-mcp] " + attachResult.message());
                System.exit(1);
                return;
            }

            try (JmxConnectionManager jmxManager = attachResult.virtualMachine().isPresent()
                    ? JmxConnectionManager.connect(attachResult.virtualMachine().get(), pid)
                    : JmxConnectionManager.connectLocal()) {

                report = client.inspectBeans(
                    pid,
                    attachResult.virtualMachine().orElse(null),
                    jmxManager.getMBeanServerConnection(),
                    filter
                );
            } catch (Exception e) {
                System.err.println("[jvm-mcp] Error inspecting Spring beans: " + e.getMessage());
                System.exit(1);
                return;
            }
        }

        if (detailBeanName != null && !detailBeanName.isBlank()) {
            printBeanDetail(client, report, detailBeanName);
        } else {
            printBeansOverview(report);
        }
    }

    private void printBeansOverview(SpringBeansReport report) {
        System.out.println("=".repeat(90));
        System.out.printf(" SPRING BEANS INSPECTION (PID: %d, Source: %s)%n", report.pid(), report.discoverySource());
        System.out.println("=".repeat(90));
        System.out.printf("Total Beans Matched: %d%n", report.totalBeans());

        if (report.totalBeans() == 0) {
            System.out.println("No beans matched the specified criteria or context is not accessible.");
            return;
        }

        for (SpringContextBeans context : report.contexts()) {
            System.out.println("-".repeat(90));
            System.out.printf("CONTEXT: %s (Parent: %s, Count: %d)%n", 
                context.contextId(), 
                context.parentId() != null ? context.parentId() : "none",
                context.beanCount()
            );
            System.out.println("-".repeat(90));
            System.out.printf("%-32s %-12s %-32s %s%n", "BEAN NAME", "SCOPE", "TYPE", "DEPENDENCIES");
            System.out.println("-".repeat(90));

            for (SpringBeanDetail bean : context.beans()) {
                String typeShort = bean.type();
                if (typeShort.length() > 30) {
                    typeShort = typeShort.substring(typeShort.lastIndexOf('.') + 1);
                }
                String depsCount = bean.dependencies().size() + " direct";

                System.out.printf("%-32s %-12s %-32s %s%n",
                    truncate(bean.name(), 30),
                    bean.scope(),
                    truncate(typeShort, 30),
                    depsCount
                );
            }
        }
    }

    private void printBeanDetail(SpringBeansClient client, SpringBeansReport report, String beanName) {
        Optional<SpringBeanDetail> detailOpt = client.getBeanDetail(report, beanName);

        if (detailOpt.isEmpty()) {
            System.err.printf("[jvm-mcp] Bean '%s' not found in active contexts.%n", beanName);
            System.exit(1);
            return;
        }

        SpringBeanDetail bean = detailOpt.get();
        System.out.println("=".repeat(80));
        System.out.printf(" BEAN DETAIL: %s%n", bean.name());
        System.out.println("=".repeat(80));
        System.out.printf("Type        : %s%n", bean.type());
        System.out.printf("Scope       : %s%n", bean.scope());
        System.out.printf("Aliases     : %s%n", bean.aliases().isEmpty() ? "none" : String.join(", ", bean.aliases()));
        System.out.printf("Resource    : %s%n", bean.resource() != null ? bean.resource() : "unknown");
        System.out.println("-".repeat(80));
        System.out.printf("DIRECT DEPENDENCIES (%d)%n", bean.dependencies().size());
        if (bean.dependencies().isEmpty()) {
            System.out.println("  (None)");
        } else {
            for (String dep : bean.dependencies()) {
                System.out.println("  -> " + dep);
            }
        }
    }

    private String truncate(String text, int max) {
        if (text == null) return "unknown";
        if (text.length() <= max) return text;
        return text.substring(0, max - 3) + "...";
    }
}
