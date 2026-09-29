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

## Active Milestone: Phase 2 — Memory, Heap & Concurrency (In Progress)

- [ ] **Memory & GC Diagnostics (`dev.jvmmcp.core.jmx.MemoryMXBeanClient`)**
  - Live heap region breakdown (Eden, Survivor, Old Gen).
  - GC pause tracker and allocation pressure score.
  - Heap class histogram via `jmap -histo:live` Attach API integration.
- [ ] **Thread & Deadlock Analysis (`dev.jvmmcp.core.jmx.ThreadMXBeanClient`)**
  - Full thread dump formatter optimized for LLM token limits.
  - Cycle detection and lock owner tracing for deadlocked threads.
  - Virtual thread (Project Loom) detection and state grouping.

---

## Upcoming Community Milestones (Open for Contributions)

The following areas are ideal for external contributors looking to claim an issue:

### 1. Spring Beans Inspector (Phase 3) — `help wanted`
- [ ] Connect to Spring Boot JMX MBean (`org.springframework.boot:type=Endpoint,name=Beans`).
- [ ] Implement glob filtering (`*Service*`, `*Repository*`).
- [ ] Extract direct dependency graphs (`@Autowired` beans, scopes, class names).

### 2. HikariCP Connection Pool Inspector (Phase 4) — `good first issue`
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
