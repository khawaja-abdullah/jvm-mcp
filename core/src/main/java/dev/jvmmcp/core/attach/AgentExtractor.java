package dev.jvmmcp.core.attach;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Handles extraction of the bundled diagnostic agent JAR into the user cache directory with checksum verification.
 */
public class AgentExtractor {

    private static final String AGENT_RESOURCE_PATH = "/agent/jvm-mcp-agent.jar";

    public Path extractAgentJar() throws IOException {
        Path cacheDir = resolveCacheDir();
        Files.createDirectories(cacheDir);

        Path targetPath = cacheDir.resolve("jvm-mcp-agent.jar");

        try (InputStream in = getClass().getResourceAsStream(AGENT_RESOURCE_PATH)) {
            if (in == null) {
                if (Files.exists(targetPath)) {
                    return targetPath;
                }
                throw new IOException("Embedded agent resource not found at " + AGENT_RESOURCE_PATH);
            }

            byte[] resourceBytes = in.readAllBytes();
            String resourceChecksum = calculateChecksum(resourceBytes);

            if (Files.exists(targetPath)) {
                String existingChecksum = calculateChecksum(Files.readAllBytes(targetPath));
                if (resourceChecksum.equals(existingChecksum)) {
                    return targetPath;
                }
            }

            Files.write(targetPath, resourceBytes);
            return targetPath;
        }
    }

    public Path resolveCacheDir() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null && !localAppData.isBlank()) {
                return Path.of(localAppData, "jvm-mcp");
            }
            return Path.of(System.getProperty("user.home"), ".jvm-mcp");
        }
        return Path.of(System.getProperty("user.home"), ".cache", "jvm-mcp");
    }

    public String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm missing in standard runtime", e);
        }
    }
}
