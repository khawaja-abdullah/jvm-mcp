package dev.jvmmcp.core.attach;

import dev.jvmmcp.core.model.JvmProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JvmAttachServiceTest {

    private JvmAttachService attachService;

    @BeforeEach
    void setUp() {
        attachService = new JvmAttachService();
    }

    @Test
    @DisplayName("listJvms should find active JVM processes including current process")
    void shouldListRunningJvmsIncludingSelf() {
        List<JvmProcess> jvms = attachService.listJvms();

        assertThat(jvms).isNotNull();
        assertThat(jvms).isNotEmpty();

        long currentPid = ProcessHandle.current().pid();
        boolean foundSelf = jvms.stream().anyMatch(jvm -> jvm.pid() == currentPid);

        assertThat(foundSelf)
            .as("listJvms() must discover the test runner JVM itself")
            .isTrue();
    }

    @Test
    @DisplayName("attach with non-existent PID should gracefully return not found error")
    void shouldHandleNonExistentPidGracefully() {
        String nonExistentPid = "999999999";

        AttachResult result = attachService.attach(nonExistentPid);

        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.status()).isIn(AttachStatus.PROCESS_NOT_FOUND, AttachStatus.GENERIC_ERROR);
    }

    @Test
    @DisplayName("attach with blank PID should return generic error")
    void shouldHandleBlankPidGracefully() {
        AttachResult result = attachService.attach("   ");

        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.status()).isEqualTo(AttachStatus.GENERIC_ERROR);
    }

    private String invokeExtractMainClass(String displayName) throws Exception {
        java.lang.reflect.Method m = JvmAttachService.class.getDeclaredMethod("extractMainClass", String.class);
        m.setAccessible(true);
        return (String) m.invoke(attachService, displayName);
    }

    @Test
    @DisplayName("extractMainClass handles '-jar target/demo.jar' -> 'demo.jar'")
    void extractMainClassHandlesJarWithPath() throws Exception {
        assertThat(invokeExtractMainClass("-jar target/demo.jar")).isEqualTo("demo.jar");
    }

    @Test
    @DisplayName("extractMainClass handles '-jar demo.jar' -> 'demo.jar'")
    void extractMainClassHandlesJarSimple() throws Exception {
        assertThat(invokeExtractMainClass("-jar demo.jar")).isEqualTo("demo.jar");
    }

    @Test
    @DisplayName("extractMainClass returns main class when followed by JVM args")
    void extractMainClassHandlesMainWithArgs() throws Exception {
        assertThat(invokeExtractMainClass("org.example.Application --spring.profiles.active=dev")).isEqualTo("org.example.Application");
    }

}
