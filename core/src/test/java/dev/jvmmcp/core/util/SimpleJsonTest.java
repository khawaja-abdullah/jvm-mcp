package dev.jvmmcp.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleJsonTest {

    @Test
    @DisplayName("parseObject should parse complex nested JSON with strings, arrays, and numbers")
    void shouldParseNestedJsonObject() {
        String json = """
            {
                "serviceName": "order-service",
                "port": 8080,
                "active": true,
                "emptyValue": null,
                "tags": ["spring", "mcp", "jvm"],
                "config": {
                    "timeoutMs": 1500,
                    "ratio": 0.85
                }
            }
            """;

        Map<String, Object> map = SimpleJson.parseObject(json);

        assertThat(map).isNotNull();
        assertThat(map.get("serviceName")).isEqualTo("order-service");
        assertThat(map.get("port")).isEqualTo(8080);
        assertThat(map.get("active")).isEqualTo(true);
        assertThat(map.get("emptyValue")).isNull();

        @SuppressWarnings("unchecked")
        List<Object> tags = (List<Object>) map.get("tags");
        assertThat(tags).containsExactly("spring", "mcp", "jvm");

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) map.get("config");
        assertThat(config.get("timeoutMs")).isEqualTo(1500);
        assertThat(config.get("ratio")).isEqualTo(0.85);
    }

    @Test
    @DisplayName("parseObject should handle escaped quotes and unicode characters")
    void shouldHandleEscapeSequences() {
        String json = "{\"message\": \"Hello \\\"World\\\"\\nLine 2\", \"unicode\": \"\\u0041\"}";
        Map<String, Object> map = SimpleJson.parseObject(json);

        assertThat(map.get("message")).isEqualTo("Hello \"World\"\nLine 2");
        assertThat(map.get("unicode")).isEqualTo("A");
    }
}
