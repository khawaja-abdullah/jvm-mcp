package dev.jvmmcp;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "jvm-mcp",
    mixinStandardHelpOptions = true,
    version = JvmMcp.VERSION,
    description = "Live JVM inspection via Model Context Protocol without target dependencies.",
    subcommands = { 
        ServeCommand.class, 
        ListCommand.class, 
        MemoryCommand.class, 
        ThreadsCommand.class,
        BeansCommand.class
    }
)
public class JvmMcp implements Runnable {
    public static final String VERSION = "1.0.0-SNAPSHOT";

    @Override
    public void run() {
        // Default behavior if no subcommand is provided.
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JvmMcp()).execute(args);
        System.exit(exitCode);
    }
}
