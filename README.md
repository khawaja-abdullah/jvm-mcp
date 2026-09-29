# JVM-MCP

A native Model Context Protocol (MCP) server designed to inspect running JVM applications in real-time. 

Zero dependencies on the target application. Powered by the JVM Attach API and compiled via GraalVM for <15ms startup times.

## The Problem

If you use an AI coding assistant (Claude Desktop, Cursor) with a Java codebase, the LLM is completely blind to runtime context. It cannot see memory leaks, thread deadlocks, or actual Spring beans in memory. 

Existing solutions are either Node.js/Python wrappers that require Actuator HTTP exposure, or they rely on static code analysis. `jvm-mcp` connects directly to the JVM via the Attach API (`com.sun.tools.attach`) using just the process PID. It requires zero code changes in the target app.

## Features

* **Zero-instrumentation:** Attaches locally without needing a `-javaagent` at startup.
* **Native Binary:** Distributed as a GraalVM native image. Fast startup, no JVM installation required on the client side.
* **LLM-optimized:** Outputs structured JSON that AI models parse natively.

## Dual Architecture

This project deliberately avoids injecting Spring Boot into the default execution path to ensure immediate startup:
1. **CLI Mode (`stdio`)**: Uses pure Java MCP SDK. Starts in <15ms. Intended for local IDE integration and standard MCP operations.
2. **Web Mode (`--transport sse`)**: (Optional) Uses Spring AI MCP. Boot overhead of ~100ms. Intended for dashboard operations.

## Known Limitations

* **OS Permissions:** The Attach API requires identical OS permissions. The target JVM and `jvm-mcp` must run under the same user UID. For cross-namespace operations (e.g., Docker containers), fallback to the `--actuator` HTTP mode.
* **GraalVM Windows Support:** Due to `attach.dll` dynamic linking constraints, Windows distributions are packaged via `jpackage` (fat JAR + minimal JRE) yielding ~300ms startup times, whereas Linux/macOS ship as pure native binaries.

## Quick Start
*(WIP: Build instructions and releases coming soon)*
