package dev.jvmmcp.core.attach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AgentExtractorTest {

    private AgentExtractor agentExtractor;

    @BeforeEach
    void setUp() {
        agentExtractor = new AgentExtractor();
    }

    @Test
    @DisplayName("resolveCacheDir should return a valid non-empty path across operating systems")
    void shouldResolveValidCacheDir() {
        Path cacheDir = agentExtractor.resolveCacheDir();

        assertThat(cacheDir).isNotNull();
        assertThat(cacheDir.toString()).contains("jvm-mcp");
    }

    @Test
    @DisplayName("calculateChecksum should return consistent SHA-256 hash string")
    void shouldCalculateDeterministicChecksum() {
        byte[] sampleData = "diagnostic-agent-sample-bytes".getBytes(StandardCharsets.UTF_8);

        String checksum1 = agentExtractor.calculateChecksum(sampleData);
        String checksum2 = agentExtractor.calculateChecksum(sampleData);

        assertThat(checksum1)
            .isNotNull()
            .isEqualTo(checksum2)
            .hasSize(64); // SHA-256 hex length
    }
}
