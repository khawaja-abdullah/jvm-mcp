package dev.jvmmcp.core.jmx;

import dev.jvmmcp.core.model.HeapHistogram;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HeapHistogramReaderTest {

    @Test
    @DisplayName("parseHistogramStream should correctly parse class ranking, instance counts, and byte sizes")
    void shouldParseHistogramStream() throws Exception {
        String sampleOutput = """
             num     #instances         #bytes  class name (module)
            -------------------------------------------------------
               1:         45231        5427720  java.lang.String (java.base@21.0.3)
               2:         23104        1848320  java.util.concurrent.ConcurrentHashMap$Node (java.base@21.0.3)
               3:          5120         819200  [B (java.base@21.0.3)
               4:          1024         122880  com.example.OrderService
            Total         74479        8218120
            """;

        HeapHistogramReader reader = new HeapHistogramReader();
        ByteArrayInputStream in = new ByteArrayInputStream(sampleOutput.getBytes(StandardCharsets.UTF_8));

        HeapHistogram histogram = reader.parseHistogramStream(in, 12345L, 3);

        assertThat(histogram).isNotNull();
        assertThat(histogram.pid()).isEqualTo(12345L);
        assertThat(histogram.totalInstances()).isEqualTo(74479L);
        assertThat(histogram.totalBytes()).isEqualTo(8218120L);
        assertThat(histogram.topClasses()).hasSize(3);

        assertThat(histogram.topClasses().get(0).rank()).isEqualTo(1);
        assertThat(histogram.topClasses().get(0).className()).contains("java.lang.String");
        assertThat(histogram.topClasses().get(0).instances()).isEqualTo(45231L);
        assertThat(histogram.topClasses().get(0).bytes()).isEqualTo(5427720L);

        assertThat(histogram.topClasses().get(1).rank()).isEqualTo(2);
        assertThat(histogram.topClasses().get(1).className()).contains("ConcurrentHashMap");

        assertThat(histogram.topClasses().get(2).rank()).isEqualTo(3);
        assertThat(histogram.topClasses().get(2).className()).contains("[B");
    }
}
