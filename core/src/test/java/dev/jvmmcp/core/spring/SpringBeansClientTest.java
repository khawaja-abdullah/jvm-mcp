package dev.jvmmcp.core.spring;

import dev.jvmmcp.core.model.SpringBeanDetail;
import dev.jvmmcp.core.model.SpringBeansReport;
import dev.jvmmcp.core.model.SpringContextBeans;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SpringBeansClientTest {

    @Test
    @DisplayName("getBeanDetail should find bean by name or alias")
    void shouldFindBeanByNameOrAlias() {
        SpringBeanDetail bean1 = new SpringBeanDetail(
            "userService",
            List.of("userSvc"),
            "singleton",
            "com.example.UserService",
            null,
            List.of("userRepository")
        );
        SpringBeanDetail bean2 = new SpringBeanDetail(
            "orderService",
            List.of(),
            "singleton",
            "com.example.OrderService",
            null,
            List.of()
        );

        SpringContextBeans context = new SpringContextBeans("app", null, 2, List.of(bean1, bean2));
        SpringBeansReport report = new SpringBeansReport(12345L, "MOCK", 2, List.of(context));

        SpringBeansClient client = new SpringBeansClient();

        Optional<SpringBeanDetail> byName = client.getBeanDetail(report, "userService");
        Optional<SpringBeanDetail> byAlias = client.getBeanDetail(report, "userSvc");
        Optional<SpringBeanDetail> notFound = client.getBeanDetail(report, "nonExistent");

        assertThat(byName).isPresent();
        assertThat(byName.get().type()).isEqualTo("com.example.UserService");

        assertThat(byAlias).isPresent();
        assertThat(byAlias.get().name()).isEqualTo("userService");

        assertThat(notFound).isEmpty();
    }
}
