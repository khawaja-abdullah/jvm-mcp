# JVM-MCP

> **Servidor MCP nativo en Java para inspección en vivo de aplicaciones Spring Boot.**  
> Dale a Claude, Cursor y Antigravity contexto real sobre memoria, threads y base de datos sin modificar el código de tu app.

[![CI](https://github.com/oscarbol09/jvm-mcp/actions/workflows/ci.yml/badge.svg)](https://github.com/oscarbol09/jvm-mcp/actions/workflows/ci.yml)
[![Java Version](https://img.shields.io/badge/Java-21+-007396?logo=openjdk&logoColor=white)](https://jdk.java.net/21/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)
[![Good First Issues](https://img.shields.io/github/issues/oscarbol09/jvm-mcp/good%20first%20issue?color=7057ff&label=good%20first%20issues)](https://github.com/oscarbol09/jvm-mcp/issues)
[![GitHub Sponsors](https://img.shields.io/badge/Sponsor-GitHub-ea4aaa?logo=github-sponsors&logoColor=white)](https://github.com/sponsors/oscarbol09)
[![Support on Ko-Fi](https://img.shields.io/badge/Support-Ko--Fi-F16061?logo=ko-fi&logoColor=white)](https://ko-fi.com/oscarmb09)

---

## Tabla de Contenidos

- [El Problema de la IA Ciega](#el-problema-de-la-ia-ciega)
- [La Solución: Cero Fricción](#la-solución-cero-fricción)
- [Herramientas Expuestas](#herramientas-expuestas)
- [Principios de Arquitectura](#principios-de-arquitectura)
- [Límites y Decisiones de Diseño (Trade-offs)](#límites-y-decisiones-de-diseño-trade-offs)
- [Contribución](#contribución)
- [Autor](#autor)

---

## El Problema de la IA Ciega

Todo desarrollador backend que trabaja con asistentes de IA en repositorios Java gigantes se topa con el mismo muro: **el código estático no es la realidad en ejecución**.

```text
1. Le preguntas a Claude: "¿Por qué el pago falla a veces?"
2. La IA lee el código y dice: "El código luce correcto, podría ser un deadlock."
3. Te frustras porque la IA no puede verificar el estado de los threads reales.
4. Tienes que ir a la terminal, ejecutar `jstack <pid>`, copiar el chorro de texto,
   pegarlo en el chat, y rogar que no te corte por límite de tokens.
```

Las soluciones actuales son defectuosas: scripts en Python que analizan dumps en frío, o herramientas en Node.js que obligan a modificar el `pom.xml` de tu aplicación para agregar dependencias de Actuator o JMX remoto.

---

## La Solución: Cero Fricción

**JVM-MCP** resuelve esto conectándose localmente a la JVM mediante la **Attach API** (`com.sun.tools.attach`). 

Se distribuye como un único binario nativo que arranca en `< 15ms`. Sin instalar Node, sin scripts intermedios, y lo más importante: **sin tocar una sola línea de código en tu aplicación objetivo**.

```
┌───────────────────────────────────────┐
│     Claude Desktop / Cursor / IDE     │
└──────────────────┬────────────────────┘
                   │ MCP Protocol (stdio)
                   ▼
┌───────────────────────────────────────┐
│              jvm-mcp                  │ (Native Binary, < 15ms startup)
└──────────────────┬────────────────────┘
                   │ Attach API / JMX
                   ▼
┌───────────────────────────────────────┐
│        Tu App Java (PID 45231)        │ (Sin dependencias adicionales)
└───────────────────────────────────────┘
```

---

## Herramientas Expuestas

Una vez conectado, el LLM obtiene superpoderes de diagnóstico en tiempo real:

* 🚀 **Contexto Spring Boot:** Lista beans instanciados (`list_spring_beans`) y detecta componentes que tardaron demasiado en inicializar.
* 🧠 **Memoria y Heap:** Uso real del heap, recuento de pausas GC (`get_heap_summary`) y top N clases que más RAM consumen.
* 🧵 **Threads & Concurrencia:** Extracción de *thread dumps* legibles y detección automática de *deadlocks*.
* 🛢️ **HikariCP:** Inspecciona *connection leaks* o agotamiento del pool en vivo.
* 🐘 **PostgreSQL:** Explora el esquema de la base de datos y detecta *slow queries* o índices faltantes (conexión directa JDBC).

---

## Principios de Arquitectura

- **SDK Puro por defecto:** El modo CLI estándar está construido sobre el MCP SDK puro (`io.modelcontextprotocol.sdk:mcp`) vía Picocli, evadiendo el overhead de inicialización de Spring Boot para mantener el arranque por debajo de los 15ms.
- **Inyección Transparente:** Usa un `DiagnosticAgent` empaquetado en recursos y extraído en caché local (`~/.cache/jvm-mcp/`) para inyectar *MBeans* si la aplicación destino no expone diagnóstico estándar.

---

## Límites y Decisiones de Diseño (Trade-offs)

* **Restricción de OS Permissions:** Por seguridad del kernel, la *Attach API* exige que `jvm-mcp` y la aplicación objetivo se ejecuten bajo el mismo UID. Si tu app corre en Docker, la conexión directa del host fallará. Para estos escenarios, JVM-MCP expone un modo alternativo `--actuator http://localhost:8080`.
* **Soporte Windows:** El empaquetado `native-image` de GraalVM en Windows sufre bloqueos con el enlazado dinámico de `attach.dll`. De forma pragmática, los releases de Linux y macOS son ejecutables nativos, mientras que en Windows se usa un Fat JAR embebido con JRE (`jpackage`), elevando el arranque a ~300ms, pero eliminando la fricción de instalación.

---

## Contribución

Si quieres agregar herramientas, revisar `CONTRIBUTING.md`.

---

## Autor

Creado por **Oscar**.

[![GitHub Sponsors](https://img.shields.io/badge/Sponsor-GitHub-ea4aaa?logo=github-sponsors&logoColor=white)](https://github.com/sponsors/oscarbol09)
[![Support on Ko-Fi](https://img.shields.io/badge/Support-Ko--Fi-F16061?logo=ko-fi&logoColor=white)](https://ko-fi.com/oscarmb09)
