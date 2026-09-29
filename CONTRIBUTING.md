# Contribución a JVM-MCP

¡Gracias por tu interés en mejorar JVM-MCP!

## Filosofía de PRs

Mantenemos este repositorio limpio, técnico y pragmático:
1. **Atómico:** Un PR, un problema resuelto.
2. **Contexto:** Usa Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`).
3. **Cero Slop AI:** Si usas IA para generar la descripción del PR, por favor edítala para que suene a un humano. Sin "En resumen", sin "Adicionalmente", solo el qué, el por qué y el cómo.

## Configuración Local

1. Requiere **Java 21** instalado.
2. Clona el repositorio e instala las dependencias mediante Maven Wrapper (`./mvnw clean install`).
3. (Opcional) Instala GraalVM si vas a compilar el módulo `native`.

## Correr las pruebas
```bash
./mvnw clean verify
```

Cualquier duda, abre un Issue marcado con la etiqueta `question`.
