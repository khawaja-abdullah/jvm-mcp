package dev.jvmmcp.core.spring;

import dev.jvmmcp.core.model.SpringBeanDetail;
import dev.jvmmcp.core.model.SpringBeansReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SpringBeansParserTest {

    @Test
    @DisplayName("parse should accurately extract beans, scopes, types, and dependencies from Spring Boot Actuator JSON")
    void shouldParseActuatorBeansJson() {
        String actuatorPayload = """
            {
              "contexts": {
                "application": {
                  "beans": {
                    "orderService": {
                      "aliases": ["orderSvc"],
                      "scope": "singleton",
                      "type": "com.example.service.OrderService",
                      "resource": "class path resource [com/example/service/OrderService.class]",
                      "dependencies": ["orderRepository", "paymentClient"]
                    },
                    "paymentClient": {
                      "aliases": [],
                      "scope": "prototype",
                      "type": "com.example.client.PaymentClient",
                      "resource": null,
                      "dependencies": []
                    }
                  },
                  "parentId": null
                }
              }
            }
            """;

        SpringBeansParser parser = new SpringBeansParser();
        SpringBeansReport report = parser.parse(45231L, "ACTUATOR_HTTP (http://localhost:8080)", actuatorPayload);

        assertThat(report).isNotNull();
        assertThat(report.pid()).isEqualTo(45231L);
        assertThat(report.discoverySource()).contains("ACTUATOR_HTTP");
        assertThat(report.totalBeans()).isEqualTo(2);
        assertThat(report.contexts()).hasSize(1);

        assertThat(report.contexts().get(0).contextId()).isEqualTo("application");
        assertThat(report.contexts().get(0).parentId()).isNull();

        Optional<SpringBeanDetail> orderService = report.getAllBeans().stream()
            .filter(b -> "orderService".equals(b.name()))
            .findFirst();

        assertThat(orderService).isPresent();
        assertThat(orderService.get().type()).isEqualTo("com.example.service.OrderService");
        assertThat(orderService.get().scope()).isEqualTo("singleton");
        assertThat(orderService.get().aliases()).containsExactly("orderSvc");
        assertThat(orderService.get().dependencies()).containsExactly("orderRepository", "paymentClient");
    }
}
