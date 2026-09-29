package dev.jvmmcp;

import dev.jvmmcp.core.attach.JvmAttachService;
import dev.jvmmcp.core.model.JvmProcess;
import picocli.CommandLine.Command;

import java.util.List;

@Command(
    name = "list",
    aliases = {"ps", "ls"},
    description = "Lists all running Java Virtual Machines on the host",
    mixinStandardHelpOptions = true
)
public class ListCommand implements Runnable {

    @Override
    public void run() {
        JvmAttachService service = new JvmAttachService();
        List<JvmProcess> jvms = service.listJvms();

        if (jvms.isEmpty()) {
            System.out.println("No running JVM processes discovered.");
            return;
        }

        System.out.printf("%-10s %-16s %-30s %s%n", "PID", "FRAMEWORK", "MAIN CLASS", "DISPLAY NAME");
        System.out.println("-".repeat(80));

        for (JvmProcess jvm : jvms) {
            String mainClass = jvm.mainClass();
            if (mainClass.length() > 28) {
                mainClass = mainClass.substring(0, 25) + "...";
            }
            System.out.printf(
                "%-10d %-16s %-30s %s%n",
                jvm.pid(),
                jvm.framework().getDisplayName(),
                mainClass,
                jvm.displayName()
            );
        }
    }
}
