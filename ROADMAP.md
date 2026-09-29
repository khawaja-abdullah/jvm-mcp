# Project Roadmap & Contribution Scope

> Real-time JVM diagnostic tools for AI coding assistants via Model Context Protocol (MCP).

This roadmap outlines current development milestones, architectural priorities, and high-impact areas open for community contributions.

---

## Current Status: Phase 1 (Completed)

- [x] **Phase 0: Architecture Scaffolding**
  - Maven multi-module architecture (`core`, `agent`, `mcp-server`, `native`).
  - Dual-layer design (Pure Java SDK for `< 15ms` CLI startup vs optional Spring AI SSE transport).
  - GitHub Actions CI matrix on Temurin JDK 21.

- [x] **Phase 1: JVM Discovery & Attach Lifecycle**
  - JDK Attach API wrapper (`JvmAttachService`) for live process discovery.
  - Framework heuristics (`FrameworkDetector` for Spring Boot, Quarkus, Micronaut, Plain Java).
  - Lazy diagnostic agent extractor with SHA-256 caching in `~/.cache/jvm-mcp/`.
  - Tabular CLI commands (`jvm-mcp list`, `ps`, `ls`).

---

- [x] **Phase 2: Memory, Heap & Concurrency Diagnostics**
  - Live heap region breakdown (Eden, Survivor, Old Gen) & allocation pressure evaluation (`MemoryMXBeanClient`).
  - GC pause tracker and diagnostic recommendations (`get_heap_summary`, `detect_memory_pressure`).
  - Heap class histogram via `jcmd GC.class_histogram` Attach API stream parsing (`HeapHistogramReader`).
  - Structured thread dumps and deadlock cycle reconstruction (`ThreadMXBeanClient`).
  - CLI diagnostic commands (`jvm-mcp memory <pid>`, `jvm-mcp threads <pid>`).

---

## Active Milestone: Phase 3 — Spring Beans Inspector (In Progress)

- [ ] **Spring Context Introspection (`dev.jvmmcp.core.spring.SpringBeansClient`)**
  - Connect to Spring Boot JMX MBean (`org.springframework.boot:type=Endpoint,name=Beans`).
  - Fast actuator HTTP fallback probe (`/actuator/beans`).
  - Glob filtering (`*Service*`, `*Repository*`).
---

## Upcoming Community Milestones (Open for Contributions)

The following areas are ideal for external contributors looking to claim an issue:

### 1. Spring Beans Inspector (Phase 3) — `help wanted`
- [ ] Query `com.zaxxer.hikari:type=Pool (*)` MBeans.
- [ ] Calculate pool saturation ratio (`active / max`) and acquire latency warnings.
- [ ] Multi-datasource support (handling multiple distinct Hikari pools in a single JVM).

### 3. PostgreSQL Schema & Stat Inspector (Phase 5) — `help wanted`
- [ ] Pure JDBC metadata reader for table sizes, column definitions, and foreign keys.
- [ ] Query `pg_stat_user_tables` to identify sequential scan bottlenecks.
- [ ] Query `pg_stat_statements` for slow execution queries.

### 4. Remote Spring Boot Actuator Client (Phase 6) — `good first issue`
- [ ] Lightweight `java.net.http.HttpClient` client for `/actuator/health`, `/actuator/metrics`, and `/actuator/startup`.
- [ ] Basic Auth and Bearer token header propagation.

### 5. Packaging & Distribution (Phase 7 & 8)
- [ ] GraalVM Native Image tracing agent automation on Linux/macOS.
- [ ] `jpackage` bundling for zero-dependency Windows `.exe`.
- [ ] Homebrew Formula (`brew install oscarbol09/tap/jvm-mcp`).

---

## How to Claim an Area

1. Check existing issues or open a new one with the title `[Proposal] Milestone Name`.
2. Follow the architectural constraints in [`CONTRIBUTING.md`](CONTRIBUTING.md).
3. Open a draft PR early for architectural alignment.
